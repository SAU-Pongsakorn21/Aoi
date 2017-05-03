package sau.comsci.com.aoi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ShowExtraDetail extends AppCompatActivity {
    String name,detail,photo;
    TextView txt_ext_titile, txt_ext_detail;
    ImageView img_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_extra_detail);
        Bundle bundle = getIntent().getExtras();
        txt_ext_detail = (TextView) findViewById(R.id.txt_ext_detial2);
        txt_ext_titile = (TextView) findViewById(R.id.txt_ext_title2);
        img_photo = (ImageView) findViewById(R.id.img_ext_thumbnail);


        if(bundle != null)
        {
            if(bundle.getString("page").equals("page01"))
            {
                name = bundle.getString("name_place");
                detail = bundle.getString("name_detail");
                photo = bundle.getString("name_photo");
                ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
                ImageLoader.getInstance().init(imageLoaderConfiguration);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage("http://argeosau.xyz/"+photo,img_photo);
                txt_ext_titile.setText(name);
                txt_ext_detail.setText(detail);


            }
            else if(bundle.getString("page").equals("page02"))
            {
                name = bundle.getString("name_place");
                detail = bundle.getString("name_detail");
                photo = bundle.getString("name_photo");
                ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
                ImageLoader.getInstance().init(imageLoaderConfiguration);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage("http://argeosau.xyz/"+photo,img_photo);
                txt_ext_titile.setText(name);
                txt_ext_detail.setText(detail);
            }
        }
    }
}
