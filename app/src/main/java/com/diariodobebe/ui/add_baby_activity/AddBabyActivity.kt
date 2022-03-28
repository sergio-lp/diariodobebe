package com.diariodobebe.ui.add_baby_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddBabyBinding

class AddBabyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBabyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBabyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        
    }
}