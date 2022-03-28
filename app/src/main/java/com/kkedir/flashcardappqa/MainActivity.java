package com.kkedir.flashcardappqa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final boolean[] isShowingAnswers = {false};
    boolean wrongAnswers = false;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    TextView FlashcardQuestion;
    TextView Answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        initFlashcard();

        TextView flashcardQuestion = findViewById(R.id.Flashcard_Question);
        TextView flashcardAnswer = findViewById(R.id.Flashcard_Answer);

        View.OnClickListener flipFlashcard = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int questionVisibility = flashcardQuestion.getVisibility();
                int answerVisibility = flashcardAnswer.getVisibility();

                flashcardQuestion.setVisibility(answerVisibility);
                flashcardAnswer.setVisibility(questionVisibility);

            }
        };
        flashcardAnswer.setOnClickListener(flipFlashcard);
        flashcardQuestion.setOnClickListener(flipFlashcard);



        findViewById(R.id.toggle_choices_visibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView possibleAnswer1 = findViewById(R.id.answer_box);
                possibleAnswer1.setVisibility(4 - possibleAnswer1.getVisibility());

            }
        });
        findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetActivity();
            }
        });

        findViewById(R.id.Plus_Sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , add_card.class);
                MainActivity.this.startActivityForResult(intent, 200);

            }
        });
        findViewById(R.id.edit_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , add_card.class);
                Flashcard currentFlashcard = allFlashcards.get(currentCardDisplayedIndex);
                intent.putExtra("questionValue" , currentFlashcard.getQuestion());
                intent.putExtra("answerValue" , currentFlashcard.getAnswer());
                MainActivity.this.startActivityForResult(intent, 100);

            }
        });

        findViewById(R.id.next_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCardDisplayedIndex++;
                ResetActivity();
            }
        });

        findViewById(R.id.delete_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardDatabase.deleteCard(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                allFlashcards.remove(currentCardDisplayedIndex);

                if (currentCardDisplayedIndex >= allFlashcards.size()) currentCardDisplayedIndex = allFlashcards.size()-1;
//                setRandomQuestion();
                ResetActivity();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            Snackbar.make(findViewById(R.id.Flashcard_Question),
                    "Card successfully created",
                    Snackbar.LENGTH_SHORT)
                    .show();
            String questionValue = data.getExtras().getString("questionValue");
            String answerValue = data.getExtras().getString("answerValue");

            Flashcard newFlashcard = new Flashcard(questionValue, answerValue);
            if (requestCode == 200) {
                flashcardDatabase.insertCard(newFlashcard);
                allFlashcards.add(newFlashcard);
            }
            else if (requestCode == 100){
                Flashcard cardEdited = allFlashcards.get(currentCardDisplayedIndex);
                cardEdited.setQuestion(questionValue);
                cardEdited.setAnswer(answerValue);
                flashcardDatabase.updateCard(cardEdited);
                allFlashcards.set(currentCardDisplayedIndex, newFlashcard);
            }
            ResetActivity();

        }
    }
    public void setPossibleAnswerOrder (TextView [] possibleAnswers){
        Random random = new Random();
        int correctAnswerIndex = random.nextInt(3);

        Flashcard currentFlashCard = allFlashcards.get(currentCardDisplayedIndex);
        String [] list = {currentFlashCard.getAnswer() , currentFlashCard.getWrongAnswer1() , currentFlashCard.getWrongAnswer2()};
        int listIndex = 1; // the answer is at index 0
        for (int i = 0; i < 3; i++){
            if (i == correctAnswerIndex){
                possibleAnswers[i].setText(list[0]);
                possibleAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // view.setBackgroundColor(getColor(R.color.green));
                    }
                });
            }
            else{
                possibleAnswers[i].setText(list[listIndex++]);
                possibleAnswers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //view.setBackgroundColor(getColor(R.color.red));
                        //possibleAnswers[correctAnswerIndex].setBackgroundColor(getColor(R.color.green));
                    }
                });
            }
        }
    }


    // Set the flashcard to default
    // All possible answers (INVISIBLE by default)
    // Update the next and back button
    public void ResetActivity(){ // Reset to default
        allFlashcards = flashcardDatabase.getAllCards();


        findViewById(R.id.Flashcard_Question).setVisibility(View.VISIBLE);
        findViewById(R.id.Flashcard_Answer).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.Flashcard_Question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
        ((TextView)findViewById(R.id.Flashcard_Answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());



        ((ImageView)findViewById(R.id.toggle_choices_visibility)).setVisibility(wrongAnswers ? View.VISIBLE : View.INVISIBLE);
        isShowingAnswers[0] = false;

        updateNextAndBackBottom();
        ((ImageView)findViewById(R.id.delete_sign)).setVisibility((allFlashcards.size() <= 1) ? View.INVISIBLE : View.VISIBLE);
    }

    public void setRandomQuestion (){
        Random random = new Random();
        int temp = random.nextInt(allFlashcards.size());
        if (temp == currentCardDisplayedIndex) // in case the random question is the same
            temp = (temp + 1) % allFlashcards.size();
        currentCardDisplayedIndex = temp;
    }
    public void updateNextAndBackBottom(){
        ((ImageView)findViewById(R.id.next_sign)).setVisibility(currentCardDisplayedIndex < allFlashcards.size()-1 ? View.VISIBLE : View.INVISIBLE);

    }

    public void initFlashcard(){
        flashcardDatabase.initFirstCard();
        allFlashcards = flashcardDatabase.getAllCards();

        ResetActivity();


    }

}