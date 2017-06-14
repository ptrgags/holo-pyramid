package io.github.ptrgags.holopyramid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class ModelSelectActivity : AppCompatActivity() {
    /** Display names for each model */
    private val modelNames = listOf(
            "Utah Teapot", "Icosahedron", "Spider", "Steam Train")
    /** Corresponding resource IDs */
    private val modelIds = listOf(
            R.raw.utah_teapot_obj,
            R.raw.icosahedron_obj,
            R.raw.spider_obj,
            R.raw.steam_train_obj)

    /** reference to the list view */
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_select)

        // Set a custom title for the activity.
        setTitle(R.string.model_select_label)

        // Save a reference to the list view
        listView = findViewById(R.id.model_select_list) as ListView

        // Make a list item for each model
        listView.adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, modelNames)

        // When a model name is tapped, go to the hologram activity
        // and pass in the model ID.
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
