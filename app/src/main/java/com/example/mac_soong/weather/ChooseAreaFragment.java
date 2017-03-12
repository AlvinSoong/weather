package com.example.mac_soong.weather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac_soong.weather.db.City;
import com.example.mac_soong.weather.db.County;
import com.example.mac_soong.weather.db.Province;
import com.example.mac_soong.weather.util.HttpUtil;
import com.example.mac_soong.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by mac_soong on 2017/3/12.
 * <p>
 * 创建碎片 将全国各地 地名显示出来
 */

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CTTY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private Button bt_Back;
    private TextView title_Text;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private List<String> dataList = new ArrayList<>();

    /**
     * 显示省 市 县 的列表
     */
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;


    /**
     * 选中当前的省 市 以及当前的级别
     */
    private int currentLevel;
    private Province selectedProvince;
    private City selectedCity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        bt_Back = (Button) view.findViewById(R.id.back_button);
        title_Text = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);

        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CTTY) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });
        bt_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CTTY){
                    queryProvince();
                }else if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        queryProvince();
    }


    private void queryProvince() {
        bt_Back.setVisibility(View.GONE);
        title_Text.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String url = "http://guolin.tech/api/china";
            queryFromServer(url, "province");
        }
    }

    private void queryCity() {
        bt_Back.setVisibility(View.VISIBLE);
        title_Text.setText(selectedProvince.getProvinceName());

        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);

        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CTTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String url = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(url, "city");
        }
    }

    private void queryCounty() {
        title_Text.setText(selectedCity.getCityName());
        bt_Back.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(url, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败 检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            } else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}









