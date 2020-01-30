package zen.airport.inventorymanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Scanning extends AppCompatActivity {

    IntentIntegrator intentIntegrator;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //result.getContents() = secretKey + salt
                showDetails(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showDetails(String contents) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        String secret = contents.substring(0, Admin.KEY_SIZE);
        String uppu = contents.substring(Admin.KEY_SIZE);
        Log.d("", "breakTheCode: " + secret + "\n" + uppu);
        EncryptIDGenerator encryptIDGenerator = new EncryptIDGenerator(secret, uppu);
        String location;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            location = encryptIDGenerator.encrypt(contents);
            try {
                FirebaseFirestore.getInstance()
                        .collection("Inventory")
                        .document(location)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Inventory inventory = task.getResult().toObject(Inventory.class);
                                    Toast.makeText(Scanning.this, inventory.getName(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Scanning.this, inventory.getDate(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Scanning.this, inventory.getLocation(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Scanning.this, inventory.getSerialNumber(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Scanning.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                            }
                        });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator
                .setBeepEnabled(true)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setBarcodeImageEnabled(true)
                .setCameraId(0)
                .initiateScan();

    }


}