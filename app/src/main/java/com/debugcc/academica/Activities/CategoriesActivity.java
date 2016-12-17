package com.debugcc.academica.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.debugcc.academica.R;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        findViewById(R.id.category_ingenieria).setOnClickListener(this);
        findViewById(R.id.category_social).setOnClickListener(this);
        findViewById(R.id.category_biomedica).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(CategoriesActivity.this, CategoryActivity.class);
        switch (view.getId()) {
            case R.id.category_ingenieria:
                intent.putExtra("CATEGORY_EVENT", "Ingenieria");
                startActivity(intent);
                break;
            case R.id.category_social:
                intent.putExtra("CATEGORY_EVENT", "Social");
                startActivity(intent);
                break;
            case R.id.category_biomedica:
                intent.putExtra("CATEGORY_EVENT", "Biomedica");
                startActivity(intent);
                break;
        }
    }
}
