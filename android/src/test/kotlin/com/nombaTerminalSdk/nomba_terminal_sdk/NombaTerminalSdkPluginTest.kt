package com.nombaTerminalSdk.nomba_terminal_sdk

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.Test
import org.mockito.Mockito

/*
 * This demonstrates a simple unit test of the Kotlin portion of this plugin's implementation.
 *
 * Once you have built the plugin's example app, you can run these tests from the command
 * line by running `./gradlew testDebugUnitTest` in the `example/android/` directory, or
 * you can run them directly from IDEs that support JUnit such as Android Studio.
 */

internal class NombaTerminalSdkPluginTest {
  @Test
  fun onMethodCall_handleTerminalRequest_returnsExpectedValue() {
    val plugin = NombaTerminalSdkPlugin()

    val call = MethodCall("handleTerminalRequest", arrayOf("card_payment_action", "2", "1234567890", ""))

    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(call, mockResult)

    Mockito.verify(mockResult).success("")
  }
}
