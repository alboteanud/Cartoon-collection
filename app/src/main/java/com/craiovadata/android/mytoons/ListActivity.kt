package com.craiovadata.android.mytoons

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.craiovadata.android.mytoons.data.MyRepository
import timber.log.Timber
import timber.log.Timber.DebugTree


class ListActivity : AppCompatActivity() {
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        adapter = RecyclerViewAdapter(this)
        setupRecyclerView(findViewById(R.id.item_list))

        val repo = MyRepository.getInstance(this)
        val factory: ViewModelProvider.Factory = MyViewModelFactory(repo)
        val myViewModel = ViewModelProvider(this, factory).get(ListViewModel::class.java)


        myViewModel.items.observe(this, { items ->
            // update UI
            Timber.d("received items in UI")

            adapter.values = items
            adapter.notifyDataSetChanged()
        })

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

}