package com.developndesign.salonvendor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.developndesign.salonvendor.R;
import com.developndesign.salonvendor.fragment.salondetailsfragment.SalonTimingFragment;
import com.developndesign.salonvendor.model.Days;
import com.developndesign.salonvendor.model.NearBySalonModelData;
import com.developndesign.salonvendor.model.daysname.Friday;
import com.developndesign.salonvendor.model.daysname.Monday;
import com.developndesign.salonvendor.model.daysname.Saturday;
import com.developndesign.salonvendor.model.daysname.Sunday;
import com.developndesign.salonvendor.model.daysname.Thursday;
import com.developndesign.salonvendor.model.daysname.Tuesday;
import com.developndesign.salonvendor.model.daysname.Wednesday;

public class SalonTimingActivity extends AppCompatActivity {
    SalonTimingFragment salonTimingFragment;
    NearBySalonModelData nearBySalonModelData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_details);
        nearBySalonModelData=(NearBySalonModelData) getIntent().getSerializableExtra("salon");
        Toolbar toolbar = findViewById(R.id.salon_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Salon Timing" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        salonTimingFragment = new SalonTimingFragment(nearBySalonModelData);
        Days d = new Days();
        d.setMonday(new Monday());
        d.setTuesday(new Tuesday());
        d.setWednesday(new Wednesday());
        d.setThursday(new Thursday());
        d.setFriday(new Friday());
        d.setSaturday(new Saturday());
        d.setSunday(new Sunday());
        nearBySalonModelData.setDays(d);
        goToFragment(salonTimingFragment);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            Intent intent = getIntent();
            setResult(RESULT_OK,intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
    }
}