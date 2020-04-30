package com.bunbeauty.ideal.myapplication.chat

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import java.util.*

class DialogsActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel {

    private lateinit var progressBar: ProgressBar
    private lateinit var manager: FragmentManager
    private lateinit var dialogList: ArrayList<Dialog>
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDialogsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        createPanels()
        init()
    }

    private fun init() {
        recyclerView = findViewById(R.id.resultsDialogsRecycleView)
        progressBar = findViewById(R.id.progressBarDialogs)
        noDialogsText = findViewById(R.id.noDialogsDialogsText)
        dialogList = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        /*recyclerView.setLayoutManager(layoutManager)*/
        manager = supportFragmentManager
    }

    private fun createPanels() {
        createBottomPanel(supportFragmentManager)
        createTopPanel("Диалоги", ButtonTask.NONE, supportFragmentManager)
    }

}