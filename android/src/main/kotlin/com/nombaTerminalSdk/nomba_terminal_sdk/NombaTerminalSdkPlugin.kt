package com.nombaTerminalSdk.nomba_terminal_sdk

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** NombaTerminalSdkPlugin */
class NombaTerminalSdkPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    companion object {
        private const val ARGS_TRANSACTION_REQUEST_CODE = 1943
        private const val ARGS_PRINT_RECEIPT_EVENT = 1944
        private const val ARGS_GET_DEVICE_EVENT = 1945
        const val PRINT_RESULT = "PRINT_RESULT"
        const val TERMINAL_ID = "terminalId"
        const val SERIAL_NO = "serialNo"
        const val DEVICE_INFO_ARGUMENTS = "deviceInfoArguments"
        const val DEVICE_INFO_RESULT = "deviceInfoResult"
        const val DEVICE_INFO_INTENT = "com.nomba.pro.feature.device_setup.ACTION_VIEW"
        const val PAY_BY_TRANSFER_INTENT = "com.nomba.pro.feature.pay_by_transfer.ACTION_VIEW"
        const val CARD_AND_PBT_INTENT = "com.nomba.pro.feature.payment_option.ACTION_VIEW"
        const val PRINT_CUSTOM_RECEIPT_INTENT = "com.nomba.pro.core.print_receipt.ACTION_VIEW"
        const val CARD_PAYMENT = "com.nomba.pro.feature.payment_option.ACTION_VIEW"
        const val AMOUNT_DATA = "amount"
        const val MERCHANT_TX_REF = "merchantTxRef"
        const val TXN_RESULT = "txnResultData"
        const val RECEIPT_OPTIONS = "receiptOptions"
        const val ARGS_PAYMENT_OPTION_STATE = "ARGS_PAYMENT_OPTION_STATE"
        const val SDK_PAYMENT_OPTIONS = "SDK_PAYMENT_OPTIONS"
        const val ARGS_PRINT_DATA = "ARGS_PRINT_DATA"
        const val ARGS_PRINT_BITMAP_DATA = "ARGS_PRINT_BITMAP_DATA"
        private const val CHANNEL = "nomba_terminal_sdk"
    }

    private lateinit var channel: MethodChannel
    private var pendingResult: Result? = null
    private var activity: Activity? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        pendingResult = result
        when (call.method) {
            "handleTerminalRequest" -> {
                val args = call.argument<List<String>>("args")
                if (args != null) {
                    handleTerminalRequest(args)
                } else {
                    result.error("INVALID_ARGUMENT", "Invalid arguments passed", null)
                }
            }

            "getDeviceInfo" -> {
                getDeviceInfo()
            }

            else -> result.notImplemented()
        }
    }

    private fun getDeviceInfo() {
        val intent = Intent(DEVICE_INFO_INTENT)

        intent.putExtra(DEVICE_INFO_ARGUMENTS, "$TERMINAL_ID,$SERIAL_NO")
        activity?.startActivityForResult(intent, ARGS_GET_DEVICE_EVENT)
    }

    private fun handleTerminalRequest(args: List<String>) {
        val actionKey = args[0]
        val isPayment =
            actionKey != "print_custom_receipt_action" && actionKey != "get_device_info_action"
        val amount = if (isPayment) args[1] else ""
        val transactionReference = if (isPayment) args[2] else ""
        val receiptData = if (isPayment) args[3] else ""

        when (actionKey) {
            "card_payment_action" -> triggerPayment(
                CARD_PAYMENT,
                amount,
                transactionReference,
                receiptData
            )

            "pay_by_transfer_action" -> triggerPayment(
                PAY_BY_TRANSFER_INTENT,
                amount,
                transactionReference,
                receiptData
            )

            "card_payment_and_pbt_action" -> triggerCardAndPBT(
                amount,
                transactionReference,
                receiptData
            )

            "print_custom_receipt_action" -> triggerPrintCustomReceipt(args[1], args[2])
            else -> pendingResult?.error("ERROR", "Invalid terminal action", null)
        }
    }

    private fun triggerPayment(
        intentAction: String,
        amount: String,
        transactionReference: String,
        receiptData: String
    ) {
        try {
            val intent = Intent(intentAction)
            val formattedAmount = (amount.toInt() * 100).toString()
            intent.putExtra(AMOUNT_DATA, formattedAmount)
            intent.putExtra(MERCHANT_TX_REF, transactionReference)
            intent.putExtra(RECEIPT_OPTIONS, getReceiptOptions(receiptData))

            activity?.startActivityForResult(intent, ARGS_TRANSACTION_REQUEST_CODE)
        } catch (e: Exception) {
            handleError("Failed to complete payment action", e)
        }
    }

    private fun triggerCardAndPBT(
        amount: String,
        transactionReference: String,
        receiptData: String
    ) {
        try {
            val intent = Intent(CARD_AND_PBT_INTENT)
            val formattedAmount = (amount.toInt() * 100).toString()
            intent.putExtra(AMOUNT_DATA, formattedAmount)
            intent.putExtra(MERCHANT_TX_REF, transactionReference)
            intent.putExtra(RECEIPT_OPTIONS, getReceiptOptions(receiptData))
            intent.putExtra(ARGS_PAYMENT_OPTION_STATE, SDK_PAYMENT_OPTIONS)

            activity?.startActivityForResult(intent, ARGS_TRANSACTION_REQUEST_CODE)
        } catch (e: Exception) {
            handleError("Failed to complete transfer action", e)
        }
    }

    private fun triggerPrintCustomReceipt(receiptData: String, logoPath: String) {
        try {
            val intent = Intent(PRINT_CUSTOM_RECEIPT_INTENT)
            intent.putExtras(createPrintReceiptBundle(receiptData, logoPath))

            activity?.startActivityForResult(intent, ARGS_PRINT_RECEIPT_EVENT)
        } catch (e: Exception) {
            handleError("Failed to print custom receipt", e)
        }
    }

    private fun createPrintReceiptBundle(receiptData: String, logoPath: String): Bundle {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type
        val arrayList: ArrayList<HashMap<String, Any>> = gson.fromJson(receiptData, type)

        val bundle = Bundle()
        bundle.putSerializable(ARGS_PRINT_DATA, arrayList)

        val receiptLogo = bitmapFromFilePath(logoPath)
        bundle.putParcelable(ARGS_PRINT_BITMAP_DATA, receiptLogo)

        return bundle
    }

    private fun bitmapFromFilePath(filePath: String): Bitmap {
        return BitmapFactory.decodeFile(filePath)
    }

    private fun handleError(error: String, exception: Exception) {
        pendingResult?.error("ERROR", "$error:: ${exception.message}", null)
    }

    private fun getReceiptOptions(receiptData: String): String {
        val gson = Gson()
        val type = object : TypeToken<HashMap<String, Any>>() {}.type
        val hashMap: HashMap<String, Any> = gson.fromJson(receiptData, type)
        return gson.toJson(hashMap)
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

        binding.addActivityResultListener { requestCode, resultCode, data ->
            if (requestCode == ARGS_TRANSACTION_REQUEST_CODE) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val response = data?.getStringExtra(TXN_RESULT) ?: "null"
                        pendingResult?.success(response)
                    }

                    Activity.RESULT_CANCELED -> {
                        val error = data?.getStringExtra(TXN_RESULT) ?: "null"
                        pendingResult?.error("CANCELED", "Payment was canceled", error)
                    }

                    else -> {
                        pendingResult?.error("ERROR", "Payment failed with an unknown error", null)
                    }
                }
                pendingResult = null
                true
            } else if (requestCode == ARGS_GET_DEVICE_EVENT) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val type = object : TypeToken<HashMap<String, Any>>() {}.type
                        val resultJson = data?.getStringExtra(DEVICE_INFO_RESULT)

                        val gson = GsonBuilder()
                            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                            .create()

                        val deviceInfoResult: HashMap<String, String> =
                            gson.fromJson(resultJson, type)

                        val terminalId = deviceInfoResult[TERMINAL_ID]
                        val serialNo = deviceInfoResult[SERIAL_NO]

                        val response = "Terminal Id: $terminalId Serial No: $serialNo"
                        pendingResult?.success(response)
                    }

                    Activity.RESULT_CANCELED -> {
                        val error = data?.getStringExtra(TXN_RESULT) ?: "null"
                        pendingResult?.error("CANCELED", "Failed to get device info", error)
                    }

                    else -> {
                        pendingResult?.error("ERROR", "Error getting device info", null)
                    }
                }
                pendingResult = null
                true
            } else {
                pendingResult = null
                false
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}
