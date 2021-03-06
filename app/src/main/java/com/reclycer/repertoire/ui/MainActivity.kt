package com.reclycer.repertoire.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.reclycer.repertoire.R

import com.reclycer.repertoire.data.ContactService
import com.reclycer.repertoire.data.DataManager
import com.reclycer.repertoire.data.DatabaseManager
import com.reclycer.repertoire.ui.edit.AddContactActivity
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val myAdapter = ListContactAdapter()
    lateinit var datamanager: DataManager
    lateinit var databasemanager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contact_list.layoutManager = LinearLayoutManager(this)
        contact_list.adapter = myAdapter

        databasemanager = DatabaseManager(this)
        datamanager = DataManager(this)


        swipeRefresh.setOnRefreshListener { refresh(datamanager) }

    }

    private fun displayUpdatedContent() {
        myAdapter.contactList.clear()
        myAdapter.contactList.addAll(databasemanager.readContactList()!!.toList())
        myAdapter.notifyDataSetChanged()
    }

    private fun refresh(datamanager: DataManager) {
        datamanager.refresh()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<ContactService.ApiContact>> {
            override fun onSuccess(contactList: List<ContactService.ApiContact>) {
                swipeRefresh.isRefreshing = false
                displayUpdatedContent()
            }

            override fun onSubscribe(d: Disposable?) {
            }

            override fun onError(e: Throwable?) {
                swipeRefresh.isRefreshing = false
                Log.i("DataManager", "Failed to subscribe")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_add -> {
                val intent = Intent(this, AddContactActivity::class.java)
                intent.putExtra(AddContactActivity.IS_CURRENT, false)
                startActivityForResult(intent, 1)

                return true
            }
            R.id.Delete -> {
                val intent1 = Intent(this, DeleteActivity::class.java)
                startActivityForResult(intent1, 2)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1,2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    displayUpdatedContent()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh(datamanager)

        if(!databasemanager.hasCurrentUser()) {
            showCreateUserScreen()
        }else{

            displayUpdatedContent()
            Log.i("MainActivity", "Current user : ${databasemanager.currentUser()?.lastName} Token : ${databasemanager.currentUser()?.gcmToken} ")
        }
    }



    private fun showCreateUserScreen() {
        val intent = Intent(this, AddContactActivity::class.java)
        intent.putExtra(AddContactActivity.IS_CURRENT, true)
        startActivityForResult(intent, 1)
    }



}
