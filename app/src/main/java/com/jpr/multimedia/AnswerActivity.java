package com.jpr.multimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    String uid,que,postkey;
    EditText editText;
    Button button;
    AnswerMember member;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference Allquestions,ntref;
    String name,url,time;
    NewMember newMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        newMember = new NewMember();
        member=new AnswerMember();
        editText=findViewById(R.id.answer_et);
        button=findViewById(R.id.btn_answer_submit);
        Bundle bundle=getIntent().getExtras();

        if (bundle!=null){
            uid=bundle.getString("u");
            postkey=bundle.getString("p");
        }else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        Allquestions = database.getReference("All Questions").child(postkey).child("Answer");
        ntref = database.getReference("notification").child(uid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
            }
        });

    }
    void saveAnswer(){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String userid=user.getUid();

        String answer = editText.getText().toString();
        if (answer!=null){

            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            time = savedate + ":" + savetime;

            member.setAnswer(answer);
            member.setTime(time);
            member.setName(name);
            member.setUid(userid);
            member.setUrl(url);

            String id = Allquestions.push().getKey();
            Allquestions.child(id).setValue(member);

            newMember.setName(name);
            newMember.setText("Replied To your Question: " + answer);
            newMember.setSeen("no");
            newMember.setUid(userid);
            newMember.setUrl(url);


            String key = ntref.push().getKey();
            ntref.child(key).setValue(newMember);

            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Please write answer", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String userid=user.getUid();
        FirebaseFirestore d= FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference=d.collection("user").document(userid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            url = task.getResult().getString("url");
                            name = task.getResult().getString("name");


                        }else {
                            Toast.makeText(AnswerActivity.this,"Error", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}