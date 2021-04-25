package com.craiovadata.android.mytoons

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.craiovadata.android.mytoons.data.MyRepository
import timber.log.Timber
import timber.log.Timber.DebugTree


class ListActivity : AppCompatActivity() {
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var loadingIndicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        if (BuildConfig.DEBUG)
            Timber.plant(DebugTree())

        adapter = RecyclerViewAdapter(this)
        loadingIndicator = findViewById(R.id.listLoadingIndicator)
        loadingIndicator.visibility = VISIBLE
        setupRecyclerView(findViewById(R.id.item_list))

        val repo = MyRepository.getInstance(this)
        val factory: ViewModelProvider.Factory = MyViewModelFactory(repo)
        val myViewModel = ViewModelProvider(this, factory).get(ListViewModel::class.java)

        myViewModel.items.observe(this, { items ->
            loadingIndicator.visibility = View.GONE
            adapter.values = items
            adapter.notifyDataSetChanged()
        })

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

}