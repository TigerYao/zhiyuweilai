package com.zhiyuweilai.tiger.robotbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.zhiyuweilai.tiger.robotbook.act.WebViewActivity;
import com.zhiyuweilai.tiger.robotbook.data.SettingsConfig;
import com.zhiyuweilai.tiger.robotbook.mainview.LoginFragment;
import com.zhiyuweilai.tiger.robotbook.view.materialhelptutorial.TutorialItem;
import com.zhiyuweilai.tiger.robotbook.act.MaterialTutorialActivity;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements LoginFragment.OnFragmentInteractionListener{
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_LOGIN = 2;
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadTutorial();
    }

    public void loadTutorial() {
        isLogin = SettingsConfig.getInstance(this).isLogin();
        if(SettingsConfig.getInstance(this).showGuidePage()) {
            Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
            mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
            startActivityForResult(mainAct, REQUEST_CODE);
            SettingsConfig.getInstance(this).isShowGuidePage(false);
        }else if(!isLogin){
            replaceFragment(LoginFragment.newInstance("",""));
        }else{

        }

    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(R.string.slide_1_african_story_books, R.string.slide_1_african_story_books,
                R.color.slide_1, R.drawable.tut_page_1_front,  R.drawable.tut_page_1_background);

        TutorialItem tutorialItem2 = new TutorialItem(R.string.slide_2_volunteer_professionals, R.string.slide_2_volunteer_professionals_subtitle,
                R.color.slide_2,  R.drawable.tut_page_2_front,  R.drawable.tut_page_2_background);

        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.slide_3_download_and_go), null,
                R.color.slide_3, R.drawable.tut_page_3_foreground);

        TutorialItem tutorialItem4 = new TutorialItem(R.string.slide_4_different_languages, R.string.slide_4_different_languages_subtitle,
                R.color.slide_4,  R.drawable.tut_page_4_foreground, R.drawable.tut_page_4_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            replaceFragment(null);
        }
    }

    private void replaceFragment(Fragment replaceFragment){
        //getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,replaceFragment).commit();
        startActivity(new Intent(this, WebViewActivity.class));
        finish();
    }

    @Override
    public void onDismiss() {

    }
}
