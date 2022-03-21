package com.kkedir.flashcardappqa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class add_card extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        ImageView Minus = findViewById( R.id.cancel_button );
        Minus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );
        ImageView Save = findViewById( R.id.Save );
        Save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra( "Question",((EditText) findViewById(R.id.question_box)).getText().toString());
                data.putExtra( "Answer",((EditText) findViewById(R.id.answer_box)).getText().toString());
                setResult( RESULT_OK, data );
                finish();
            }
        } );

    }
}