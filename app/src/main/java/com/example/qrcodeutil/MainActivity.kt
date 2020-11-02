package com.example.qrcodeutil

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE_SCAN_ONE = 999
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                } else {
                    ToastUtils.showShort("您拒绝了如下权限")
                }
            }
    }

    private fun scanCode() {
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).create()
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode !== Activity.RESULT_OK || data == null) {
            return
        }
        if (requestCode === REQUEST_CODE_SCAN_ONE) {
            var result = data?.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
            if (result != null){
                parseResult(result)
            }else{
                tv_result.setText("扫码结果为null")
            }
        }
    }

    fun parseResult(result: HmsScan) {
        tv_result.setText("类型："+result.getScanTypeForm())
        LogUtils.e("二维码扫描结果："+result.getOriginalValue())
        tv_result.setText("二维码扫描结果："+result.getOriginalValue())
        ToastUtils.showShort("识别二维码成功")
        when (result.getScanTypeForm()){
            HmsScan.SMS_FORM -> {
                val smsContent = result.getSmsContent()
                val content = smsContent.getMsgContent()
                val phoneNumber = smsContent.getDestPhoneNumber()

            }
            HmsScan.WIFI_CONNECT_INFO_FORM -> {
                val wifiConnectionInfo = result.wiFiConnectionInfo
                val password = wifiConnectionInfo.getPassword()
                val ssidNumber = wifiConnectionInfo.getSsidNumber()
                val cipherMode = wifiConnectionInfo.getCipherMode()
            }
            HmsScan.PURE_TEXT_FORM -> {

            }
            HmsScan.URL_FORM->{
                val url = result.originalValue
                //从其他浏览器打开
                //从其他浏览器打开
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val content_url: Uri = Uri.parse(url)
                intent.data = content_url
                startActivity(Intent.createChooser(intent, "请选择浏览器"))
            }
        }
    }

    fun scanCode(view: View) {
        scanCode()
    }

    fun makeCode(view: View) {
        val content = et_content.text.toString()
        val type = HmsScan.QRCODE_SCAN_TYPE
        val width = iv_qr_code.width
        val height = iv_qr_code.height

        val options =
            HmsBuildBitmapOption.Creator()
//                .setBitmapBackgroundColor(Color.RED)
//                .setBitmapColor(Color.BLUE)
                .setBitmapMargin(0)
                .create()
        try {
            val buildBitmap = ScanUtil.buildBitmap(content, type, width, height, options)
            Glide.with(this)
                .asBitmap()
                .load(buildBitmap)
                .into(iv_qr_code)
            ToastUtils.showShort("生成二维码成功")
        } catch (e: Exception) {
        }
    }
}