package com.peze.modules.mother.exampleapp

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.peze.modules.mother.exampleapp.R
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class MainActivity : AppCompatActivity() {
    private val CHANNEL = "myChannel"
    private lateinit var methodChannel: MethodChannel
    private lateinit var flutterEngine: FlutterEngine
    private lateinit var flutterView: FlutterView
    private var flutterViewAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())

        // Ruta del punto de entrada de tu módulo de Flutter
        flutterEngine.navigationChannel.setInitialRoute("/")

        flutterView = FlutterView(this)
        flutterView.attachToFlutterEngine(flutterEngine)

        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        methodChannel.setMethodCallHandler { call, result ->
            // Manejar las llamadas de método desde Flutter
            if (call.method == "viewRedered") {
                if (call.arguments is String) {
                    // Obtener los datos enviados desde Flutter
                    val data = call.arguments as String
                    receiveDataFromFlutter(data)
                    result.success("Vista Flutter renderizada")
                    // Invocar un método en Flutter
                    methodChannel.invokeMethod("myMethod", "Hola!! desde Android enviando datos")
                } else {
                    result.error("INVALID_ARGUMENT", "Invalid argument type", null)
                }
            } else if (call.method == "someAction") {
                if (call.arguments is String) {
                    val data = call.arguments as String
                    receiveDataFromFlutter(data)
                    result.success("Data recibida correctamente en Android")
                } else {
                    result.error("INVALID_ARGUMENT", "Invalid argument type", null)
                }
            } else {
                result.notImplemented()
            }
        }

        val myButton = findViewById<Button>(R.id.myButton)
        myButton.setOnClickListener {
            // Llama al método en Flutter al hacer clic en el botón

            if (!flutterViewAdded) {
                Handler().postDelayed({
                    // Agrega la vista de Flutter después de un retraso de 200ms
                    setContentView(flutterView)
                    flutterViewAdded = true
                }, 500)
            }
            enviarMensajeAFlutter("Hola desde Kotlin")
        }
    }

    private fun enviarMensajeAFlutter(mensaje: String) {
        // Invoca el método en Flutter a través del canal de método
        methodChannel.invokeMethod("myMethod", mensaje)
    }

    private fun receiveDataFromFlutter(data: String) {
        println("Data received from Flutter: $data")
    }
}
