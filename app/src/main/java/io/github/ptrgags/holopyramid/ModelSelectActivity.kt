package io.github.ptrgags.holopyramid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class ModelSelectActivity : AppCompatActivity() {
    private val modelNames = listOf("Utah Teapot", "Icosahedron")
    private val modelIds = listOf(R.raw.utah_teapot_obj, R.raw.icosahedron_obj)
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_select)

        // Save a reference to the list view
        listView = findViewById(R.id.model_select_list) as ListView

        listView.adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, modelNames)

        val context: Context = this
        listView.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, i, _ ->
            val modelId = modelIds[i]
            val intent = Intent(context, HoloPyramidActivity::class.java)
            intent.putExtra("model_id", modelId)
            startActivity(intent)
        }
    }
}
