package com.dreams.chat.viewHolders;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreams.chat.R;
import com.dreams.chat.interfaces.OnMessageItemClick;
import com.dreams.chat.models.Attachment;
import com.dreams.chat.models.AttachmentTypes;
import com.dreams.chat.models.Message;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.FileUtils;
import com.dreams.chat.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

/**
 * Created by a_man on 02-02-2018.
 */

public class MessageAttachmentRecordingViewHolder extends BaseMessageViewHolder {
    TextView text;
    TextView durationOrSize;
    LinearLayout ll;
    ProgressBar progressBar;
    ImageView playPauseToggle;
    private Message message;
    private File file;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    private TextView statusText;
    private ArrayList<Message> messages;
    private LinearLayout backGround;
    private ImageView user_image;


    private RecordingViewInteractor recordingViewInteractor;

    public MessageAttachmentRecordingViewHolder(View itemView, OnMessageItemClick itemClickListener,
                                                RecordingViewInteractor recordingViewInteractor, ArrayList<Message> messages) {
        super(itemView, itemClickListener, messages);
        text = itemView.findViewById(R.id.text);
        durationOrSize = itemView.findViewById(R.id.duration);
        ll = itemView.findViewById(R.id.container);
        progressBar = itemView.findViewById(R.id.progressBar);
        playPauseToggle = itemView.findViewById(R.id.playPauseToggle);
        statusImg = itemView.findViewById(R.id.statusImg);
        statusLay = itemView.findViewById(R.id.statusLay);
        statusText = itemView.findViewById(R.id.statusText);
        backGround = itemView.findViewById(R.id.backGround);
        user_image = itemView.findViewById(R.id.user_image);
        this.messages = messages;
        this.recordingViewInteractor = recordingViewInteractor;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.CHAT_CAB)
                    downloadFile();
                onItemClick(true);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClick(false);
                return true;
            }
        });
    }

    @Override
    public void setData(Message message, int position, HashMap<String, User> myUsers, ArrayList<User> myUsersList) {
        super.setData(message, position, myUsers, myUsersList);

        this.message = message;
        Attachment attachment = message.getAttachment();

        boolean loading = message.getAttachment().getUrl().equals("loading");
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        playPauseToggle.setVisibility(loading ? View.GONE : View.VISIBLE);

        file = new File(Environment.getExternalStorageDirectory() + "/"
                +
                context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(message.getAttachmentType()) + (isMine() ? "/.sent/" : "")
                , message.getAttachment().getName());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context, uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millis = Integer.parseInt(durationStr);
                durationOrSize.setText(TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + TimeUnit.MILLISECONDS.toSeconds(millis));
                mmr.release();
            } catch (Exception e) {
            }
        } else
            durationOrSize.setText(FileUtils.getReadableFileSize(attachment.getBytesCount()));

        if (message.getAttachment().getName().contains(".wav")) {
            File newFile = new File(Environment.getExternalStorageDirectory() + "/"
                    +
                    context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(message.getAttachmentType()) + (isMine() ? "/.sent/" : "")
                    , message.getAttachment().getName().replace(".wav", ".mp3"));

            Log.e("isRecordingPlaying", "" + recordingViewInteractor.isRecordingPlaying(
                    message.getAttachment().getName().replace(".wav", ".mp3")));

            playPauseToggle.setImageDrawable(ContextCompat.getDrawable(context, newFile.exists() ? (recordingViewInteractor.isRecordingPlaying(
                    message.getAttachment().getName().replace(".wav", ".mp3"))
                    ? R.drawable.ic_stop : R.drawable.ic_play_circle_outline) : R.drawable.ic_file_download_accent_36dp));
        } else {
            Log.e("isRecordingPlaying", "" + recordingViewInteractor.isRecordingPlaying(
                    message.getAttachment().getName()));
            playPauseToggle.setImageDrawable(ContextCompat.getDrawable(context, file.exists() ? recordingViewInteractor.isRecordingPlaying(
                    message.getAttachment().getName()) ? R.drawable.ic_stop : R.drawable.ic_play_circle_outline : R.drawable.ic_file_download_accent_36dp));
        }

        //cardView.setCardBackgroundColor(ContextCompat.getColor(context, message.isSelected() ? R.color.colorPrimary : R.color.colorBgLight));
        // ll.setBackgroundColor(message.isSelected() ? ContextCompat.getColor(context, R.color.colorPrimary) : isMine() ? Color.WHITE : ContextCompat.getColor(context, R.color.colorBgLight));

        if (isMine()) {
            backGround.setBackgroundResource(R.drawable.shape_incoming_message);
            text.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            durationOrSize.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            senderName.setVisibility(View.GONE);
            senderName.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            user_image.setVisibility(View.GONE);
        } else {
            backGround.setBackgroundResource(R.drawable.shape_outgoing_message);
//            senderName.setVisibility(View.VISIBLE);
            text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            durationOrSize.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            senderName.setTextColor(context.getResources().getColor(R.color.textColor4));
            user_image.setVisibility(View.VISIBLE);
            try {
                Picasso.get()
                        .load(myUsers.get(message.getSenderId()).getImage())
                        .tag(context)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(user_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (message.getStatusUrl() != null && !message.getStatusUrl().isEmpty()) {
            statusLay.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(message.getStatusUrl())
                    .tag(context)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(statusImg);
            statusText.setText("Status");
        } else if (message.getReplyId() != null && !message.getReplyId().equalsIgnoreCase("0")) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getId() != null &&
                        messages.get(i).getId().equalsIgnoreCase(message.getReplyId())) {
                    statusLay.setVisibility(View.VISIBLE);
                    Message message1 = messages.get(i);
                    if (message1.getAttachmentType() == AttachmentTypes.AUDIO) {
                        Picasso.get()
                                .load(R.drawable.ic_audiotrack_24dp)
                                .tag(context)
                                .placeholder(R.drawable.ic_audiotrack_24dp)
                                .into(statusImg);
                        statusText.setText("Audio");
                    } else if (message1.getAttachmentType() == AttachmentTypes.RECORDING) {
                        Picasso.get()
                                .load(R.drawable.ic_audiotrack_24dp)
                                .tag(context)
                                .placeholder(R.drawable.ic_audiotrack_24dp)
                                .into(statusImg);
                        statusText.setText("Recording");
                    } else if (message1.getAttachmentType() == AttachmentTypes.VIDEO) {
                        if (message1.getAttachment().getData() != null) {
                            Picasso.get()
                                    .load(message1.getAttachment().getData())
                                    .tag(context)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(statusImg);
                            statusText.setText("Video");
                        } else
                            statusImg.setBackgroundResource(R.drawable.ic_placeholder);
                        //replyName.setText(message1.getAttachment().getName());
                    } else if (message1.getAttachmentType() == AttachmentTypes.IMAGE) {
                        if (message1.getAttachment().getUrl() != null) {
                            Picasso.get()
                                    .load(message1.getAttachment().getUrl())
                                    .tag(context)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(statusImg);
                            statusText.setText("Image");
                        } else
                            statusImg.setBackgroundResource(R.drawable.ic_placeholder);
                    } else if (message1.getAttachmentType() == AttachmentTypes.CONTACT) {
                        Picasso.get()
                                .load(R.drawable.ic_person_black_24dp)
                                .tag(context)
                                .placeholder(R.drawable.ic_person_black_24dp)
                                .into(statusImg);
                        statusText.setText("Contact");
                    } else if (message1.getAttachmentType() == AttachmentTypes.LOCATION) {
                        try {
                            String staticMap = "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=16&size=512x512&format=png";
                            String Key = "&key="+"YOUR_API_KEY";
                            String latitude, longitude;
                            JSONObject placeData = new JSONObject(message1.getAttachment().getData());
                            statusText.setText(placeData.getString("address"));
                            latitude = placeData.getString("latitude");
                            longitude = placeData.getString("longitude");
                            Picasso.get()
                                    .load(String.format(staticMap, latitude, longitude) + Key)
                                    .tag(context)
                                    .into(statusImg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (message1.getAttachmentType() == AttachmentTypes.DOCUMENT) {
                        Picasso.get()
                                .load(R.drawable.ic_insert_64dp)
                                .tag(context)
                                .placeholder(R.drawable.ic_insert_64dp)
                                .into(statusImg);
                        statusText.setText("Document");
                    } else if (message1.getAttachmentType() == AttachmentTypes.NONE_TEXT) {
                        statusText.setText(message1.getBody());
                        statusImg.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            statusLay.setVisibility(View.GONE);
        }
    }

    //@OnClick(R.id.playPauseToggle)
    public void downloadFile() {
        if (file.exists()) {
            if (message.getAttachment().getName().contains(".m4a") || message.getAttachment().getName().contains(".wav")) {
                File newFile = new File(Environment.getExternalStorageDirectory() + "/"
                        +
                        context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(message.getAttachmentType()) + (isMine() ? "/.sent/" : "")
                        , message.getAttachment().getName().replace(".wav", ".mp3"));
                if (newFile.exists()) {
                    recordingViewInteractor.playRecording(newFile,
                            message.getAttachment().getName().replace(".wav", ".mp3"), getAdapterPosition());
                } else {
                    convertAudio();
                }

            } else {
                recordingViewInteractor.playRecording(file, message.getAttachment().getName(), getAdapterPosition());
            }

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri uri = MyFileProvider.getUriForFile(context,
//                    context.getString(R.string.authority),
//                    file);
//            intent.setDataAndType(uri, Helper.getMimeType(context, uri)); //storage path is path of your vcf file and vFile is name of that file.
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            context.startActivity(intent);
        } else if (!isMine() && !message.getAttachment().getUrl().equals("loading")) {
           /* if (message.getAttachment().getName().contains(".m4a")) {
                convertAudio();
            } else {*/
            broadcastDownloadEvent();
            //}

        } else {
            Toast.makeText(context, "File unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public interface RecordingViewInteractor {
        boolean isRecordingPlaying(String fileName);

        void playRecording(File file, String fileName, int position);
    }

    private void convertAudio() {
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // Toast.makeText(context, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
                recordingViewInteractor.playRecording(new File(convertedFile.getPath()), message.getAttachment().getName()
                        .replace(".wav", ".mp3"), getAdapterPosition());

                /*playPauseToggle.setImageDrawable(ContextCompat.getDrawable(context, new File(convertedFile.getPath()).exists() ?
                        recordingViewInteractor.isRecordingPlaying(message.getAttachment().getName()) ?
                                R.drawable.ic_stop : R.drawable.ic_play_circle_outline : R.drawable.ic_file_download_accent_36dp));*/
            }

            @Override
            public void onFailure(Exception error) {
                //      Toast.makeText(context, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        //  Toast.makeText(context, "Converting audio file...", Toast.LENGTH_SHORT).show();
        AndroidAudioConverter.with(context)
                .setFile(file)
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert();
    }

}
