package com.moinapp.wuliao.modules.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.modules.wowo.PostDetailActivity;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/30.
 */
public class MyReplyAdapter extends BaseAdapter {
    private ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    private Context mContext;
    protected LayoutInflater mInflater;
    protected ImageLoader imageLoader;
    private List<PostInfo> mPostList;
    private int mColumn;
    /**
     * 用户选择的Reply
     */
    public static ArrayList<PostInfo> mSelectedPost = new ArrayList<PostInfo>();

    static int count;

    public static int mMode;

    public MyReplyAdapter(Context context, List<PostInfo> postlist, int column) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mPostList = postlist;
        this.mColumn = column;
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
    }

    @Override
    public int getCount() {
        return mPostList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPostList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_post_list_item, null);
            viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContentView(viewHolder, position);
        return convertView;
    }

    private void setContentView(final ViewHolder viewHolder, final int position) {
        if(mPostList == null || mPostList.size() <= position) {
            return;
        }

        final PostInfo mPostItem = mPostList.get(position);
        if (mPostItem.getStatus() != 3) { //3是精华贴
            viewHolder.mSuggestImage.setVisibility(View.GONE);
        }
        if (!mPostItem.isHasImage()) {
            viewHolder.mPicImage.setVisibility(View.GONE);
        }
        if (!mPostItem.isHasEmoji()) {
            viewHolder.mEmojiImage.setVisibility(View.GONE);
        }
        viewHolder.mPostTitle.setText(mPostItem.getTitle());
        if(mColumn == 0) {
            viewHolder.mPostContent.setVisibility(View.VISIBLE);
            viewHolder.mAuthorLayout.setVisibility(View.GONE);
//            viewHolder.mPostContent.setText(mPostItem.getContent());
        } else if(mColumn == 1) {
            viewHolder.mPostContent.setVisibility(View.GONE);
            viewHolder.mAuthorLayout.setVisibility(View.VISIBLE);

            if (mPostItem.getAuthor() != null) {
                try {
                    imageLoader.displayImage(mPostItem.getAuthor().getAvatar().getUri(), viewHolder.mAuthorAvatar, BitmapUtil.getImageLoaderOption());
                } catch (NullPointerException e) {
                    MyLog.e(e);
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
                viewHolder.mAuthorName.setText(mPostItem.getAuthor().getNickname());
                if(mPostItem.getAuthor().getLevel() != 10000) {
                    viewHolder.mAuthorLevel.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.mAuthorLevel.setVisibility(View.VISIBLE);
                }
            }
        }
        viewHolder.mWoName.setText(mPostItem.getWoname());
        viewHolder.mPostTime.setText(StringUtil.formatDate(mPostItem.getUpdatedAt(), IPResourceConstants.IP_RELEASE_DATE_FORMAT));
        viewHolder.mCommentCount.setText(mPostItem.getCommentCount()+"");

        if(mMode == MineConstants.MODE_EDIT) {
            if(mPostItem.getStatus() != 3 || mColumn == 1) { // 3：精华帖
                viewHolder.mSelectIcon.setVisibility(View.VISIBLE);
                if (mSelectedPost.contains(mPostItem)) {
                    viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
                } else {
                    viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                }

                viewHolder.mPostItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSelectedPost.contains(mPostItem)) {
                            viewHolder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                            mSelectedPost.remove(mPostItem);
                            count--;
                            if (textcallback != null)
                                textcallback.onListen(count);
                        } else {
                            viewHolder.mSelectIcon.setImageResource(R.drawable.icon_select);
                            mSelectedPost.add(mPostItem);
                            count++;
                            if (textcallback != null)
                                textcallback.onListen(count);
                        }
                        MyLog.i((mColumn==0?"发帖":"评论") + " 的个数 " + mSelectedPost.size());
                        for (int i = 0; i < mSelectedPost.size(); i++) {
                            MyLog.i(i+" : " + mSelectedPost.get(i).getTitle());
                        }
                    }
                });
            }
        } else if(mMode == MineConstants.MODE_NONE) {
            viewHolder.mSelectIcon.setVisibility(View.GONE);
            viewHolder.mPostItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable(PostDetailActivity.KEY_WO_ID, mPostItem.getWoid());
                    b.putString(PostDetailActivity.KEY_POST_ID, mPostItem.getPostid());
                    MyLog.i("toPostDetail: " + b);
                    AppTools.toIntent(mContext, b, PostDetailActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);

                }
            });
        }
    }

    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.mPostItem = (LinearLayout) convertView.findViewById(R.id.id_listitem_ly);
        holder.mAuthorLayout = (RelativeLayout) convertView.findViewById(R.id.id_author_rl);
        holder.mAuthorAvatar = (ImageView)convertView.findViewById(R.id.author_avatar);
        holder.mAuthorName = (TextView) convertView.findViewById(R.id.author_name);
        holder.mAuthorLevel = (TextView) convertView.findViewById(R.id.author_level);
        holder.mSelectIcon = (ImageView) convertView.findViewById(R.id.id_item_select);
        holder.mSuggestImage = (ImageView) convertView.findViewById(R.id.post_suggest_image);
        holder.mPicImage = (ImageView) convertView.findViewById(R.id.post_pic_image);
        holder.mEmojiImage = (ImageView) convertView.findViewById(R.id.post_emoji_image);
        holder.mPostTitle = (TextView) convertView.findViewById(R.id.post_title);
        holder.mPostContent = (TextView) convertView.findViewById(R.id.post_desc);

        holder.mWoName = (TextView) convertView.findViewById(R.id.id_woname);
        holder.mPostTime = (TextView) convertView.findViewById(R.id.id_releasedate);
        holder.mCommentCount = (TextView) convertView.findViewById(R.id.id_reply_num);

        return holder;
    }

    public List<PostInfo> getPostList() {
        return mPostList;
    }

    class ViewHolder {
        LinearLayout mPostItem;
        RelativeLayout mAuthorLayout;
        ImageView mAuthorAvatar;//作者头像
        TextView mAuthorName;//作者名称
        TextView mAuthorLevel;//作者级别
        ImageView mSelectIcon; //帖子是否被选中的图标
        ImageView mSuggestImage; //帖子是精品贴的图标
        ImageView mPicImage; //帖子包含图片的图标
        ImageView mEmojiImage; //帖子包含表情的图标
        TextView mPostTitle;//帖子标题
        TextView mPostContent;//帖子内容
        TextView mWoName;//帖子所在窝窝名称
        TextView mPostTime;//帖子更新时间
        TextView mCommentCount;//评论数
    }

    public static ArrayList<PostInfo> getSelectedPost() {
        return mSelectedPost;
    }

    public static void setSelectedPost(ArrayList<PostInfo> selectedPost) {
        mSelectedPost = selectedPost;
        count = mSelectedPost.size();
    }

    public static void setMode(int mode) {
        mMode = mode;
    }

    private TextCallback textcallback = null;
    public interface TextCallback {
        void onListen(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

}
