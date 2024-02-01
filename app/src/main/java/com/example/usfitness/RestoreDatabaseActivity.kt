package com.example.usfitness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.example.usfitness.ui.theme.AppTheme
import com.example.usfitness.viewmodel.MainScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

@AndroidEntryPoint
class RestoreDatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            AppTheme {
                Column {
                    com.google.android.material.progressindicator.LinearProgressIndicator(this@RestoreDatabaseActivity)
                }
            }
        }

        val mainScreenViewModel : MainScreenViewModel by viewModels<MainScreenViewModel>()

        val data: Uri? = intent?.clipData?.getItemAt(0)?.uri

        val context = this

        val file = File(this.filesDir,"temp.usdb")

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                data?.let { contentResolver.openInputStream(it).use{
                        inputStream->
                    file.outputStream().use {
                        if (inputStream != null) {
                            it.write(inputStream.readBytes())
                        }
                    }
                } }
            }

            if (intent?.type?.startsWith("application/") == true
            ) {

                mainScreenViewModel.restoreToDatabase(context.filesDir,file)
                startActivity(Intent(context,MainActivity::class.java))
                finish()
            }else {
                Toast.makeText(context,"Invalid File ${file.path}",Toast.LENGTH_SHORT).show()
            }
        }





    }

}