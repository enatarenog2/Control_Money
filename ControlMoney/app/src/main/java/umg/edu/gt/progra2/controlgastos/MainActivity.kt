package umg.edu.gt.progra2.controlgastos

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnFechaNacimiento: Button
    private lateinit var btnCrearCuenta: Button

    private var fechaNacimiento = ""

    private val preferencias by lazy {
        getSharedPreferences("datos_usuario", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editar = intent.getBooleanExtra("editar_datos", false)

        // Si ya hay datos y no estamos editándolos,
        // abrimos directamente la pantalla principal.
        if (preferencias.contains("nombre") && !editar) {
            abrirInicio()
            return
        }

        setContentView(R.layout.activity_main)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCorreo = findViewById(R.id.etCorreo)
        btnFechaNacimiento = findViewById(R.id.btnFechaNacimiento)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)

        if (editar) {
            cargarDatos()
            btnCrearCuenta.text = "Guardar cambios"
        }

        btnFechaNacimiento.setOnClickListener {
            seleccionarFecha()
        }

        btnCrearCuenta.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun seleccionarFecha() {
        val calendario = Calendar.getInstance()

        val selector = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                fechaNacimiento = String.format(
                    "%02d/%02d/%04d",
                    dia,
                    mes + 1,
                    anio
                )
                btnFechaNacimiento.text = fechaNacimiento
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        selector.datePicker.maxDate = calendario.timeInMillis
        selector.show()
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val correo = etCorreo.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingresa tu nombre"
            etNombre.requestFocus()
            return
        }

        if (apellido.isEmpty()) {
            etApellido.error = "Ingresa tu apellido"
            etApellido.requestFocus()
            return
        }

        if (correo.isEmpty() ||
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        ) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return
        }

        if (fechaNacimiento.isEmpty()) {
            Toast.makeText(
                this,
                "Selecciona tu fecha de nacimiento",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        preferencias.edit()
            .putString("nombre", nombre)
            .putString("apellido", apellido)
            .putString("correo", correo)
            .putString("fechaNacimiento", fechaNacimiento)
            .apply()

        Toast.makeText(
            this,
            "Datos guardados correctamente",
            Toast.LENGTH_SHORT
        ).show()

        abrirInicio()
    }

    private fun cargarDatos() {
        etNombre.setText(preferencias.getString("nombre", ""))
        etApellido.setText(preferencias.getString("apellido", ""))
        etCorreo.setText(preferencias.getString("correo", ""))

        fechaNacimiento =
            preferencias.getString("fechaNacimiento", "") ?: ""

        if (fechaNacimiento.isNotEmpty()) {
            btnFechaNacimiento.text = fechaNacimiento
        }
    }

    private fun abrirInicio() {
        val intent = Intent(this, InicioActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
