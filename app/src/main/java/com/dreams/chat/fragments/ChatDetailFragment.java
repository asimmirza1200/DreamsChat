package com.dreams.chat.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreams.chat.BaseApplication;
import com.dreams.chat.R;
import com.dreams.chat.adapters.GroupNewParticipantsAdapter;
import com.dreams.chat.adapters.MediaSummaryAdapter;
import com.dreams.chat.interfaces.OnUserDetailFragmentInteraction;
import com.dreams.chat.interfaces.UserGroupSelectionDismissListener;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.Message;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.ConfirmationDialogFragment;
import com.dreams.chat.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.RealmList;

public class ChatDetailFragment extends Fragment implements GroupNewParticipantsAdapter.ParticipantClickListener {
    private static final int CALL_REQUEST_CODE = 911;
    private OnUserDetailFragmentInteraction mListener;

    private View mediaSummaryContainer, mediaSummaryViewAll, userDetailContainer, groupDetailContainer;
    private RecyclerView mediaSummary;
    private TextView userPhone, userStatus, mediaCount;
    private ImageView userPhoneClick;
    private SwitchCompat muteNotificationToggle;

    private ArrayList<Message> attachments;

    private User user, userMe;
    public Group group;
    private Helper helper;

    private GroupNewParticipantsAdapter selectedParticipantsAdapter;
    private TextView participantsCount, participantsAdd;
    private ProgressBar participantsProgress;
    private ArrayList<User> groupUsers, groupNewUsers;
    private String CONFIRM_TAG = "confirm";
    private TextView blockUser;
    private LinearLayout statusLayout;
    private ArrayList<User> myUsers;

    IntentFilter filter;
    private Context context;

    public ChatDetailFragment() {
        // Required empty public constructor
    }

    public static ChatDetailFragment newInstance(User user) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.user = user;
        return fragment;
    }

    public static ChatDetailFragment newInstance(Group group) {
        ChatDetailFragment fragment = new ChatDetailFragment();
        fragment.group = group;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(getContext());
        userMe = helper.getLoggedInUser();

        if (group != null) {
            myUsers = new ArrayList<User>();
            HashMap<String, User> userHashMap = helper.getCacheMyUsers();
            if (userHashMap != null)
                myUsers.addAll(userHashMap.values());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);
        userDetailContainer = view.findViewById(R.id.userDetailContainer);
        groupDetailContainer = view.findViewById(R.id.groupDetailContainer);
        mediaCount = view.findViewById(R.id.mediaCount);
        blockUser = view.findViewById(R.id.blockUser);
        statusLayout = view.findViewById(R.id.statusLayout);
        mediaSummaryContainer = view.findViewById(R.id.mediaSummaryContainer);
        mediaSummaryViewAll = view.findViewById(R.id.mediaSummaryAll);
        mediaSummary = (RecyclerView) view.findViewById(R.id.mediaSummary);

        if (user != null) {
            userDetailContainer.setVisibility(View.VISIBLE);
            groupDetailContainer.setVisibility(View.GONE);

            userPhone = view.findViewById(R.id.userPhone);
            userPhoneClick = view.findViewById(R.id.userPhoneClick);
            userStatus = view.findViewById(R.id.userStatus);
            muteNotificationToggle = view.findViewById(R.id.muteNotificationSwitch);
        } else {
            userDetailContainer.setVisibility(View.GONE);
            groupDetailContainer.setVisibility(View.VISIBLE);

            participantsCount = view.findViewById(R.id.participantsCount);
            participantsAdd = view.findViewById(R.id.participantsAdd);
            participantsProgress = view.findViewById(R.id.participantsProgress);
            RecyclerView participantsRecycler = view.findViewById(R.id.participants);
            participantsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            groupUsers = new ArrayList<>();
            selectedParticipantsAdapter = new GroupNewParticipantsAdapter(this, ChatDetailFragment.this, groupUsers, userMe, group.getId(), group.getAdmin());
            participantsRecycler.setAdapter(selectedParticipantsAdapter);
            participantsAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<User> myUsersLeftToAdd = new ArrayList<>(myUsers);
                    myUsersLeftToAdd.removeAll(groupUsers);
                    if (myUsersLeftToAdd.isEmpty()) {
                        Toast.makeText(getContext(), "No new members to add", Toast.LENGTH_SHORT).show();
                    } else {
                        groupNewUsers = new ArrayList<>();
                        GroupMembersSelectDialogFragment.newInstance(new UserGroupSelectionDismissListener() {
                            @Override
                            public void onUserGroupSelectDialogDismiss() {
                                if (!groupNewUsers.isEmpty()) {
                                    showAddMemberConfirmationDialog();
                                }
                            }

                            @Override
                            public void selectionDismissed() {

                            }
                        }, groupNewUsers, myUsersLeftToAdd, helper.getLoggedInUser()).show(getChildFragmentManager(), "selectgroupmembers");
                    }
                }
            });
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver,
                new IntentFilter("custom-event-name"));
        return view;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null) {
                    userStatus.setText(intent.getStringExtra("status"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void showAddMemberConfirmationDialog() {
        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance("Add member" + (groupNewUsers.size() == 1 ? "" : "s"),
                String.format("Are you sure you want to add %s in this group?",
                        (groupNewUsers.size() == 1 ? groupNewUsers.get(0).getNameToDisplay() : groupNewUsers.size() + " members")),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (User userToAdd : groupNewUsers) {
                            if (group.getGrpExitUserIds() == null
                                    && !group.getUserIds().contains(userToAdd.getId())) {
                                group.getUserIds().add(userToAdd.getId());
                            } else if (group.getGrpExitUserIds() != null
                                    && !group.getGrpExitUserIds().contains(userToAdd.getId())
                                    && !group.getUserIds().contains(userToAdd.getId())) {
                                group.getUserIds().add(userToAdd.getId());
                            } else if (group.getGrpExitUserIds() != null
                                    && group.getGrpExitUserIds().contains(userToAdd.getId())) {
                                group.getGrpExitUserIds().remove(userToAdd.getId());
                            } else if (group.getGrpExitUserIds() != null
                                    && group.getGrpExitUserIds().contains(userToAdd.getId())) {
                                group.getGrpExitUserIds().remove(userToAdd.getId());
                            }
                        }

                        BaseApplication.getGroupRef().child(group.getId()).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Member" + (groupNewUsers.size() == 1 ? " added" : "s added"), Toast.LENGTH_SHORT).show();
                                groupNewUsers.clear();
                            }
                        });
                        groupUsers.addAll(groupNewUsers);
                        selectedParticipantsAdapter.notifyDataSetChanged();
                        participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < groupNewUsers.size(); i++) {
                            groupNewUsers.get(i).setSelected(false);
                        }
                    }
                });
        confirmationDialogFragment.show(getChildFragmentManager(), CONFIRM_TAG);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mListener != null)
            mListener.getAttachments();

        mediaSummaryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.switchToMediaFragment();
            }
        });
        if (user != null) {
            setData();
            muteNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    helper.setUserMute(user.getId(), b);
                }
            });

            userPhoneClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callPhone(true, user.getId());
                }
            });

            blockUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMe.getBlockedUsersIds() != null && userMe.getBlockedUsersIds().contains(user.getId())) {
                        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                                .newInstance("UnBlock", String.format("Are you sure want to unblock %s",
                                        user.getNameToDisplay()), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (userMe.getBlockedUsersIds().contains(user.getId())) {
                                                    userMe.getBlockedUsersIds().remove(user.getId());
                                                }

                                                BaseApplication.getUserRef().child(userMe.getId()).child("blockedUsersIds")
                                                        .setValue(userMe.getBlockedUsersIds())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @SuppressLint("SetTextI18n")
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                helper.setLoggedInUser(userMe);
                                                                if (userMe.getBlockedUsersIds() != null &&
                                                                        userMe.getBlockedUsersIds().contains(user.getId())) {
                                                                    blockUser.setText("Unblock");
                                                                    blockUser.setTextColor(getResources().getColor(R.color.textColor3));
                                                                    blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_gray_24dp, 0, 0, 0);
                                                                } else {
                                                                    blockUser.setText("Block");
                                                                    blockUser.setTextColor(getResources().getColor(R.color.red));
                                                                    blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_red_24dp, 0, 0, 0);
                                                                }
                                                                Toast.makeText(context, "Unblocked", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(context, "Unable to unblock user",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                        confirmationDialogFragment.show(getChildFragmentManager(), CONFIRM_TAG);
                    } else {
                        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance("Block",
                                String.format("Block %s? Blocked contacts will no longer be able to call you or send you messages.",
                                        user.getNameToDisplay()),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> userBlockedIds = new ArrayList<>();
                                        if (userMe.getBlockedUsersIds() != null && userMe.getBlockedUsersIds().size() > 0) {
                                            userBlockedIds.addAll(userMe.getBlockedUsersIds());
                                            if (!userBlockedIds.contains(user.getId()))
                                                userBlockedIds.add(user.getId());
                                        } else {
                                            userBlockedIds.add(user.getId());
                                        }
                                        userMe.getBlockedUsersIds().clear();
                                        userMe.setBlockedUsersIds(userBlockedIds);

                                        BaseApplication.getUserRef().child(userMe.getId()).child("blockedUsersIds")
                                                .setValue(userMe.getBlockedUsersIds()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                helper.setLoggedInUser(userMe);
                                                if (userMe.getBlockedUsersIds() != null &&
                                                        userMe.getBlockedUsersIds().contains(user.getId())) {
                                                    blockUser.setText("Unblock");
                                                    blockUser.setTextColor(getResources().getColor(R.color.textColor3));
                                                    blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_gray_24dp, 0, 0, 0);
                                                } else {
                                                    blockUser.setText("Block");
                                                    blockUser.setTextColor(getResources().getColor(R.color.red));
                                                    blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_red_24dp, 0, 0, 0);
                                                }
                                                Toast.makeText(context, "Blocked", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Unable to block user", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                        confirmationDialogFragment.show(getChildFragmentManager(), CONFIRM_TAG);
                    }
                }
            });
        } else {
            setParticipants();
        }


    }


    private void setParticipants() {
        groupUsers.clear();
        participantsProgress.setVisibility(View.VISIBLE);
        for (String memberId : group.getUserIds()) {
            if (group.getGrpExitUserIds() == null) {
                if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(memberId)) {
                    groupUsers.add(helper.getCacheMyUsers().get(memberId));
                } else {
                    fetchUserFromFirebase(memberId);
                }

            } else if (group.getGrpExitUserIds() != null && !group.getGrpExitUserIds().contains(memberId)) {
                if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(memberId)) {
                    groupUsers.add(helper.getCacheMyUsers().get(memberId));
                } else {
                    fetchUserFromFirebase(memberId);
                }
            }
        }
        if (group.getId() != null && group.getId().startsWith(Helper.GROUP_PREFIX)) {
//            if (group.getId().split(Helper.GROUP_PREFIX)[1].split("_")[1].equalsIgnoreCase(userMe.getId()))
            if (group.getAdmin().equalsIgnoreCase(userMe.getId()))
                participantsAdd.setVisibility(View.VISIBLE);
            else
                participantsAdd.setVisibility(View.GONE);
        }

//        if (group.getUserIds().size() == groupUsers.size()) {
//            participantsProgress.setVisibility(View.GONE);
//            selectedParticipantsAdapter.notifyDataSetChanged();
//            participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
//        } else {
//            loadMembers();
//        }
        participantsProgress.setVisibility(View.GONE);
        selectedParticipantsAdapter.notifyDataSetChanged();
        participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
    }

    private void fetchUserFromFirebase(String memberId) {
        BaseApplication.getUserRef().child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    groupUsers.add(user);
                    participantsProgress.setVisibility(View.GONE);
                    selectedParticipantsAdapter.notifyDataSetChanged();
                    participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
                } catch (Exception ex) {
                    Log.e("USER", "invalid user");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notifyGroupUpdated(Group valueGroup) {
        if (group != null && group.getId().equals(valueGroup.getId())) {
            boolean isMember = valueGroup.getUserIds().contains(userMe.getId());
//            if (!isMember) {
//                participantsAdd.setOnClickListener(null);
//                participantsAdd.setVisibility(View.GONE);
//            }

            group = valueGroup;
            setParticipants();

//            if (group.getUserIds().size() != valueGroup.getUserIds().size()) {
//                group = valueGroup;
//                setParticipants();
//            }
        }
    }

    private void loadMembers() {
        for (String memberId : group.getUserIds()) {
            if (!groupUsers.contains(new User(memberId, "", "", ""))) {
                BaseApplication.getUserRef().child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            if (User.validate(user)) {
                                if (group.getUserIds().size() == groupUsers.size()) {
                                    sortGroupUsers();
                                    participantsProgress.setVisibility(View.GONE);
                                    selectedParticipantsAdapter.notifyDataSetChanged();
                                    participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
                                } else {
                                    groupUsers.add(user);
                                    selectedParticipantsAdapter.notifyDataSetChanged();
                                    if (participantsProgress.getVisibility() == View.VISIBLE)
                                        participantsProgress.setVisibility(View.GONE);
                                    participantsCount.setText(String.format("Participants (%d)", selectedParticipantsAdapter.getItemCount()));
                                }
                            }
                        } catch (Exception ex) {
                            Log.e("USER", "invalid user");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

    }

    private void sortGroupUsers() {
        ArrayList<User> sorted = new ArrayList<>();
        for (String userId : group.getUserIds()) {
            if (group.getGrpExitUserIds() != null && !group.getGrpExitUserIds().contains(userMe.getId())) {
                int index = groupUsers.indexOf(new User(userId, "", "", ""));
                sorted.add(groupUsers.get(index));
                groupUsers.remove(index);
            }
        }
        groupUsers.clear();
        groupUsers.addAll(sorted);
    }

    private void callPhone(boolean dial, String phoneNumber) {
        if (dial) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(getContext(), "To dial number automatically for you we need this permission", Toast.LENGTH_LONG).show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
                }
            } else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            }
        } else {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callPhone(true, user.getId());
                else
                    callPhone(false, user.getId());
                break;
        }
    }

    private void setData() {
        userStatus.setText(user.getStatus());
        userPhone.setText(user.getId());
        muteNotificationToggle.setChecked(helper.isUserMute(user.getId()));
        userMe = helper.getLoggedInUser();
        if (userMe.getBlockedUsersIds() != null && userMe.getBlockedUsersIds().contains(user.getId())) {
            blockUser.setText("Unblock");
            blockUser.setTextColor(getResources().getColor(R.color.textColor3));
            blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_gray_24dp, 0, 0, 0);
        } else {
            blockUser.setText("Block");
            blockUser.setTextColor(getResources().getColor(R.color.red));
            blockUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_block_red_24dp, 0, 0, 0);
        }
        if (user.getBlockedUsersIds() != null && user.getBlockedUsersIds().contains(userMe.getId())) {
            statusLayout.setVisibility(View.GONE);
        } else {
            statusLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setupMediaSummary(ArrayList<Message> attachments) {
        if (attachments.size() > 0) {
            this.attachments = attachments;
            mediaSummaryContainer.setVisibility(View.VISIBLE);
            mediaCount.setText(String.valueOf(attachments.size()));
            mediaSummary.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mediaSummary.setAdapter(new MediaSummaryAdapter(getContext(), attachments, false, userMe.getId()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnUserDetailFragmentInteraction) {
            mListener = (OnUserDetailFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserDetailFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onParticipantClick(final int pos, final User participant) {
        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance("Remove user",
                String.format("Are you sure you want to remove %s from this group?", participant.getNameToDisplay()),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeParticipant(participant.getId());
                        groupUsers.get(pos).setSelected(false);
                        groupUsers.remove(pos);
                        selectedParticipantsAdapter.notifyItemRemoved(pos);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        confirmationDialogFragment.show(getChildFragmentManager(), CONFIRM_TAG);
    }

    private void removeParticipant(String id) {
        ArrayList<String> userIds = new ArrayList<>();

//        for (String userId : group.getUserIds()){
//            if (!userId.equals(id))
//                userIds.add(userId);
//        }

        if (group.getAdmin().equalsIgnoreCase(id)) {
            RealmList<String> dUserIds = new RealmList<>();
            dUserIds.addAll(group.getUserIds());
            dUserIds.remove(id);
            if (dUserIds != null && dUserIds.size() > 0) {
                group.setAdmin(Helper.getRandomElement(dUserIds, group.getUserIds().size()).get(0));
            }
        }
        if (group.getGrpExitUserIds() != null)
            userIds.addAll(group.getGrpExitUserIds());
        userIds.add(id);
        group.setGrpExitUserIds(userIds);
//        userIds.add(id);
//        group.setGrpExitUserIds(userIds);


//        BaseApplication.getGroupRef().child(group.getId()).child("grpExitUserIds").setValue(userIds).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(), "Member removed", Toast.LENGTH_SHORT).show();
//            }
//        });
        BaseApplication.getGroupRef().child(group.getId()).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Member removed", Toast.LENGTH_SHORT).show();
            }
        });

//        BaseApplication.getGroupRef().child(group.getId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Group group1 = dataSnapshot.getValue(Group.class);
//                if (group1.getId().equalsIgnoreCase(group.getId())) {
//                    group = group1;
//                    setParticipants();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        BaseApplication.getUserRef().child(id).child(Helper.REF_GROUP).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("tag", dataSnapshot.toString());
                ArrayList arrayList = new ArrayList();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("id").getValue().toString().equalsIgnoreCase(group.getId())) {
                        Log.d("remove id", "remove ");
//                        dataSnapshot1.getRef().removeValue();
                        // Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
