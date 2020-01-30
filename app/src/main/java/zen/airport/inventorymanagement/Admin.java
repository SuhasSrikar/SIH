package zen.airport.inventorymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Admin extends AppCompatActivity {

    ImageView imageViewQrCode;
    EditText serail, date, name, place;
    Button generate;
    long documentID;
    String encryptedDocumentID;
    String secretKey ;
    String salt;
    public static final int KEY_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        imageViewQrCode = findViewById(R.id.qrCode);
        serail = findViewById(R.id.SN);
        date = findViewById(R.id.iDate);
        name = findViewById(R.id.iName);
        place = findViewById(R.id.iPlace);
        generate = findViewById(R.id.generate);

        generate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (serail.getText().toString().equals("") ||
                        date.getText().toString().equals("") ||
                        name.getText().toString().equals("") ||
                        place.getText().toString().equals("")) {
                    Toast.makeText(Admin.this, "Incomplete data", Toast.LENGTH_SHORT).show();
                } else {
                    documentID = System.currentTimeMillis();
                    Inventory inventory = new Inventory();
                    inventory.setDate(date.getText().toString());
                    inventory.setName(name.getText().toString());
                    inventory.setLocation(place.getText().toString());
                    inventory.setSerialNumber(serail.getText().toString());
                    inventory.setTime(documentID);
                    secretKey = generateRandomID();
                    salt = generateRandomID();
                    EncryptIDGenerator encryptIDGenerator = new EncryptIDGenerator(secretKey, salt);
                    try {
                        String plain = encryptIDGenerator.encrypt(String.valueOf(documentID));
                        Log.d(" ", "onClick: "+plain);
                        FirebaseFirestore.getInstance()
                                .collection("Inventory")
                                .document(plain)
                                .set(inventory)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            try {
                                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                Bitmap bitmap = barcodeEncoder.encodeBitmap(secretKey.concat(salt),
                                                        BarcodeFormat.QR_CODE, 400, 400);
                                                imageViewQrCode.setImageBitmap(bitmap);
                                                Log.d(" ", "onComplete: Secrey key is "+secretKey+"\n"+"Salt is "+salt+"\n Secret key + salt"+secretKey.concat(salt));
                                            } catch (Exception e) {
                                                Toast.makeText(Admin.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }



    private String generateRandomID() {
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder randomID = new StringBuilder(KEY_SIZE);
        for (int i = 0; i < KEY_SIZE; i++) {
            int index = (int) (characterSet.length() * Math.random());
            randomID.append(characterSet.charAt(index));
        }
        return randomID.toString();
    }
}
