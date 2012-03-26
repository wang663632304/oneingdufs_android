package com.molice.oneingdufs.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;
import com.molice.oneingdufs.layouts.AppMenu;
import com.molice.oneingdufs.utils.FormValidator;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.SharedPreferencesStorager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户中心-个人信息视图<br />
 * R.layout.user_info
 */
public class UserInfoActivity extends Activity {
	private TextView info_username;
	private TextView info_studentId;
	// TODO 将手机输入框改为自定义的EditText，在EditText内部添加一个按钮，按下按钮时获取本机号码并填充
	private EditText info_telphone;
	private Button info_cancel;
	private Button info_submit;
	
	private SharedPreferencesStorager storager;
	private AppMenu appMenu;
	private FormValidator validator;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        
		// 设置标题栏
		ActionBarController.setTitle(this, R.string.user_info_title);
		
		// 初始化View成员
		info_username = (TextView) findViewById(R.id.user_info_username);
		info_studentId = (TextView) findViewById(R.id.user_info_studentId);
		info_telphone = (EditText) findViewById(R.id.user_info_telphone);
		info_cancel = (Button) findViewById(R.id.user_info_cancel);
		info_submit = (Button) findViewById(R.id.user_info_submit);
		
		storager = new SharedPreferencesStorager(this);
		appMenu = new AppMenu(this);
		// 自动填充手机号码
		info_telphone.setText(storager.get("phoneNumber", ""));
		
		// 表单验证器
		JSONArray form = new JSONArray();
		form.put(FormValidator.createInputData(R.id.user_info_email, "email", R.id.user_info_email_label, "^([\\w\\d_\\.-]+)@([\\w\\d_-]+\\.)+\\w{2,4}$|^.{0}$", R.string.user_info_email_label, R.string.user_info_email_error));
		form.put(FormValidator.createInputData(R.id.user_info_truename, "truename", R.id.user_info_truename_label, "^[\u00b7\u4e00-\u9fa5]*$|^.{0}$", R.string.user_info_truename_label, R.string.user_info_truename_error));
		form.put(FormValidator.createInputData(R.id.user_info_telphone, "telphone", R.id.user_info_telphone_label, "^\\d{11}$|^.{0}$", R.string.user_info_telphone_label, R.string.user_info_telphone_error));
		form.put(FormValidator.createInputData(R.id.user_info_cornet, "cornet", R.id.user_info_cornet_label, "^\\d{3,6}$|^.{0}$", R.string.user_info_cornet_label, R.string.user_info_cornet_error));
		form.put(FormValidator.createInputData(R.id.user_info_qq, "qq", R.id.user_info_qq_label, "^\\d{0,10}$|^.{0}$", R.string.user_info_qq_label, R.string.user_info_qq_error));
		// 表单name在这里被setTag
		validator = new FormValidator(this, form);
		// 开启失去焦点时自动验证
		validator.addOnFocusChangeValidate();
		// 从本地存储中恢复数据
		validator.setInputFromLocalStorage("info_");
		// 因为恢复数据了，所以要重新更新oriInputsValue
		validator.updateOriInputsValue();

		// 显示用户名、学号
		if(storager.isExist("username")) {
			info_username.setText(storager.get("username", "当前用户"));
		}
		if(storager.isExist("studentId")) {
			info_studentId.setText(storager.get("studentId", ""));
		}
		
		info_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validator.isFormModified()) {
					if(validator.isFormCorrect()) {
						// 验证通过，发送请求，保存到本地
						JSONObject input = validator.getInput();
						// 更新输入值
						validator.updateOriInputsValue();
						// 保存数据到本地
						validator.setInputToLocalStorager("info_");
						Toast.makeText(UserInfoActivity.this, "个人信息已保存", Toast.LENGTH_SHORT).show();
						Log.d("UserInfo验证通过", input.toString());
					} else {
						ProjectConstants.alertDialog(UserInfoActivity.this, "输入错误", "请按照提示修改", true);
					}
				} else {
					Toast.makeText(UserInfoActivity.this, "无修改", Toast.LENGTH_SHORT).show();
				}
			}
		});
		info_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validator.checkBackIfFormModified();
			}
		});
	}
	
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	appMenu.onCreateOptionsMenu(menu);
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d("MainActivity", "onOptionsItemSelected被调用");
    	return appMenu.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(storager.get("isLogin", false)) {
    		// 显示登录组，隐藏未登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, false);
    		menu.setGroupVisible(AppMenu.ISLOGIN, true);
    	} else {
    		// 显示未登录组，隐藏登录组
    		menu.setGroupVisible(AppMenu.NOTLOGIN, true);
    		menu.setGroupVisible(AppMenu.ISLOGIN, false);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		validator.checkBackIfFormModified();
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
