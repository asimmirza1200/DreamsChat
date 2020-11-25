package com.dreams.chat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.dreams.chat.R;
import com.dreams.chat.adapters.AdapterSubmittedPicsRecipe;
import com.dreams.chat.adapters.ChatAdapter;
import com.dreams.chat.fragments.HomeFragment;
import com.dreams.chat.fragments.MyChallengesFragment;
import com.dreams.chat.fragments.MyRecipeFragment;
import com.dreams.chat.fragments.OptionsFragment;
import com.dreams.chat.interfaces.HomeIneractor;
import com.dreams.chat.models.Chat;
import com.dreams.chat.models.Contact;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.Status;
import com.dreams.chat.models.User;
import com.dreams.chat.services.FetchMyUsersService;
import com.dreams.chat.utils.Constants;
import com.dreams.chat.utils.Helper;
import com.dreams.chat.views.MyRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends BaseActivity implements HomeIneractor {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private RelativeLayout createPost;
    private static String OPTIONS_MORE = "optionsmore";
    private final int CONTACTS_REQUEST_CODE = 321;
    private ArrayList<Group> myGroups = new ArrayList<>();
    private ArrayList<Contact> contactsData = new ArrayList<>();

    public static ArrayList<User> myUsers = new ArrayList<>();

    @Override
    void userAdded(User value) {
        if (value.getId().equals(userMe.getId()))
            return;
        else if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(value.getId())) {
            value.setNameInPhone(helper.getCacheMyUsers().get(value.getId()).getNameToDisplay());
            addUser(value);
        } else {
            for (Contact savedContact : contactsData) {
                if (Helper.contactMatches(value.getId(), savedContact.getPhoneNumber())) {
                    value.setNameInPhone(savedContact.getName());
                    addUser(value);
                    helper.setCacheMyUsers(myUsers);
                    break;
                }
            }
        }
    }
    private void sortMyGroupsByName() {
        Collections.sort(myGroups, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                return group1.getName().compareToIgnoreCase(group2.getName());
            }
        });
    }

    private void sortMyUsersByName() {
        Collections.sort(myUsers, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getNameToDisplay().compareToIgnoreCase(user2.getNameToDisplay());
            }
        });
    }
    private void addUser(User value) {
        if (!myUsers.contains(value)) {
            myUsers.add(value);
            sortMyUsersByName();
//            menuUsersRecyclerAdapter.notifyDataSetChanged();
//            refreshUsers(-1);
        }
    }


    @Override
    void groupAdded(Group group) {
        if (!myGroups.contains(group)) {
            myGroups.add(group);
            sortMyGroupsByName();
        }
    }
    private void setProfileImage(ImageView imageView) {
        if (userMe != null)
//            Glide.with(this).load(userMe.getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_placeholder)).into(imageView);

            if (userMe != null && userMe.getImage() != null && !userMe.getImage().isEmpty()) {
                Picasso.get()
                        .load(userMe.getImage())
                        .tag(this)
                        .error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar)
                        .into(imageView);
            } else if (group != null && group.getImage() != null && !group.getImage().isEmpty()) {
                Picasso.get()
                        .load(group.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_avatar)
                        .into(imageView);

            }

        /*Glide.with(this)
                .load(userMe.getImage())
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);*/

    }


    @Override
    public void myUsersResult(ArrayList<User> myUsers) {
        helper.setCacheMyUsers(myUsers);
        this.myUsers.clear();
        this.myUsers.addAll(myUsers);
    }

    @Override
    public void myContactsResult(ArrayList<Contact> myContacts) {
        contactsData.clear();
        contactsData.addAll(myContacts);
        // MyUsersFragment myUsersFragment = ((MyUsersFragment) adapter.getItem(0));
        // if (myUsersFragment != null) myUsersFragment.setUserNamesAsInPhone();
        //  MyCallsFragment myCallsFragment = ((MyCallsFragment) adapter.getItem(2));
        //  if (myCallsFragment != null) myCallsFragment.setUserNamesAsInPhone();
    }

    @Override
    void userUpdated(User value) {
        if (value.getId().equals(userMe.getId())) {
            userMe = value;
//            setProfileImage(usersImage);
        } else if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(value.getId())) {
            value.setNameInPhone(helper.getCacheMyUsers().get(value.getId()).getNameToDisplay());
            updateUser(value);
        } else {
            for (Contact savedContact : contactsData) {
                if (Helper.contactMatches(value.getId(), savedContact.getPhoneNumber())) {
                    value.setNameInPhone(savedContact.getName());
                    updateUser(value);
                    helper.setCacheMyUsers(myUsers);
                    break;
                }
            }
        }
    }

    private void updateUser(User value) {
        int existingPos = myUsers.indexOf(value);
        if (existingPos != -1) {
            myUsers.set(existingPos, value);
//            menuUsersRecyclerAdapter.notifyItemChanged(existingPos);
//            refreshUsers(existingPos);
        }
    }

    @Override
    void groupUpdated(Group group) {
        int existingPos = myGroups.indexOf(group);
        if (existingPos != -1) {
            myGroups.set(existingPos, group);
            //menuUsersRecyclerAdapter.notifyItemChanged(existingPos);
            //refreshUsers(existingPos);
        }
    }

    @Override
    void statusAdded(Status status) {

    }

    @Override
    void statusUpdated(Status status) {

    }

    @Override
    void onSinchConnected() {

    }

    @Override
    void onSinchDisconnected() {

    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.navigation_sms:
                            openFragment(new MyChallengesFragment());
                            return true;
                        case R.id.navigation_notifications:
                            openFragment(new MyRecipeFragment());
                            return true;
                        case R.id.navigation_addchallage:
                            addchallage();
                            return true;
                        case R.id.navigation_setting:
                            OptionsFragment.newInstance(getSinchServiceInterface()).show(getSupportFragmentManager(), OPTIONS_MORE);
                            return true;
                    }
                    return false;
                }

                private void addchallage() {
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(HomeActivity.this);
                    builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                    builder.setTitle("Select Type!");
                    builder.setItems(new String[]{"Create Challenge", "Add Recipe"}, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {
                            HomeFragment.scrollposition=0;
                            if (index==0){
                                Intent recipe_intent=new Intent(HomeActivity.this, CreateChallengeActivity.class);
                                startActivity(recipe_intent);
                            }else {
                                Intent recipe_intent=new Intent(HomeActivity.this, AddRecipeActivity.class);
                                startActivity(recipe_intent);
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
//        recyclerView =findViewById(R.id.recycler_view);
//        createPost =findViewById(R.id.createpost);
//        ImageView users_image = findViewById(R.id.users_image);
//
//        users_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                OptionsFragment.newInstance(null).show(getSupportFragmentManager(), OPTIONS_MORE);
//
//            }
//        });
//        ImageView more = findViewById(R.id.more);
        Realm.init(this);

        fetchContacts();

//        more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popup = new PopupMenu(HomeActivity.this, more);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater()
//                        .inflate(R.menu.popup_menu, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Intent intent = new Intent(getApplicationContext(),MyPostsActivity.class);
//
//                        startActivity(intent);
//                        return true;
//                    }
//                });
//
//                popup.show();
//            }
//        });
//        createPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(HomeActivity.this);
//                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
//                builder.setTitle("Select Type!");
//                builder.setItems(new String[]{"Create Challenge", "Add Recipe"}, new DialogInterface.OnClickListener() {
//
//                @Override
//                    public void onClick(DialogInterface dialogInterface, int index) {
//                    if (index==0){
//                        Intent recipe_intent=new Intent(HomeActivity.this,CreateChallengeActivity.class);
//                        startActivity(recipe_intent);
//                    }else {
//                        Intent recipe_intent=new Intent(HomeActivity.this,AddRecipeActivity.class);
//                       startActivity(recipe_intent);
//                    }
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        });
//        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh_lay);
//        mySwipeRefreshLayout.setRefreshing(false);
//        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//               // getData();
//            }
//        });
      //  getData();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_REQUEST_CODE:
                fetchContacts();
                break;
        }
    }
    private void fetchContacts() {
        Helper helper = new Helper(HomeActivity.this);

        User userMe = helper.getLoggedInUser();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (!FetchMyUsersService.STARTED) {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                FetchMyUsersService.startMyUsersService(HomeActivity.this, userMe.getId(), idToken);
                            }
                        }
                    });
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE);
        }
    }
    void  getData(){


      FirebaseDatabase.getInstance().getReference().child("public")
              .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                      List<RecipeModel> list=new ArrayList<>();
                      for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          RecipeModel recipe = snapshot.getValue(RecipeModel.class);//I'm assuming you have a Recipe class
                          recipe.setKey(snapshot.getKey());
                          list.add(recipe);
                      }
                      Collections.reverse(list);
                      AdapterSubmittedPicsRecipe postadapter = new AdapterSubmittedPicsRecipe(list, HomeActivity.this);
                      recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                      recyclerView.setAdapter(postadapter);
                      mySwipeRefreshLayout.setRefreshing(false);

                  }
                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                      mySwipeRefreshLayout.setRefreshing(false);

                  }
              });

    }

    @Override
    public User getUserMe() {
        return userMe;
    }

    @Override
    public ArrayList<Contact> getLocalContacts() {
        return contactsData;
    }
}