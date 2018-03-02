package com.moinapp.wuliao.modules.ipresource;

import android.app.Activity;
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
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.modules.wowo.PostDetailActivity;
import com.moinapp.wuliao.modules.wowo.WowoContants;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.SuggestEmoji;
import com.moinapp.wuliao.modules.wowo.model.SuggestIP;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
* Created by liujiancheng on 15/6/10.
 * 注意这是IP首页中窝窝热帖的adaptor
*/
public class WoPostAdaptor extends BaseAdapter {
    ILogger MyLog = LoggerFactory.getLogger(WoPostAdaptor.class.getSimpleName());
    private Activity mContext = null;
    private List<PostInfo> mPostList;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;

    public WoPostAdaptor(Activity context, List<PostInfo> postInfos) {
        mContext = context;
        mPostList = postInfos;
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        if (mLayoutInflater == null) {
            mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public int getCount() {
        return mPostList != null ? mPostList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mPostList != null ? mPostList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.wowo_tz_list_item, null);
            viewHolder = getListViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewHolder) convertView.getTag();
        }

        setContentView(viewHolder, position);

        return convertView;
    }

    public void updatePostList() {

    }

    private void setContentView(ListViewHolder viewHolder, int position) {
        if (mPostList != null && mPostList.size() > 0) {
            final PostInfo postInfo = mPostList.get(position);
            //设置每一个view
            if (postInfo.getCover() != null) {
                try {
                    mImageLoader.displayImage(postInfo.getCover().getUri(), viewHolder.mPostCover, BitmapUtil.getImageLoaderOption());
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
                //帖子封面点击

                viewHolder.mPostCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(postInfo.getCover().getUri());
                        Intent intent = new Intent(mContext, StillsViewPagerActivity.class);
                        intent.putExtra(StillsViewPagerActivity.KEY_CURRENT, postInfo.getCover().getUri());
                        intent.putExtra(StillsViewPagerActivity.KEY_CLIPLIST, list);
                        MyLog.i("click post cover: current=" + postInfo.getCover().getUri());
                        mContext.startActivity(intent);**/
                        gotoPostDetail(postInfo);
                    }
                });
            }
            if (postInfo.getAuthor().getAvatar() != null) {
                try {
                    mImageLoader.displayImage(postInfo.getAuthor().getAvatar().getUri(), viewHolder.mAuthorAvatar, BitmapUtil.getImageLoaderOption());
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }
            viewHolder.mAuthorAvatar.setOnClickListener(null);
            viewHolder.mAuthorName.setText(postInfo.getAuthor().getNickname());
            viewHolder.mAuthorName.setOnClickListener(null);

            if (postInfo.getAuthor().getLevel() != 10000) {
                viewHolder.mAuthorLevel.setVisibility(View.INVISIBLE);
            }
            viewHolder.mPostUpdateAt.setText(StringUtil.humanDate(postInfo.getUpdatedAt(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
            if (postInfo.getStatus() != 3) { //3是精华贴
                viewHolder.mSuggestImage.setVisibility(View.GONE);
            }
            if (!postInfo.isHasImage()) {
                viewHolder.mPicImage.setVisibility(View.GONE);
            }
            if (!postInfo.isHasEmoji()) {
                viewHolder.mEmojiImage.setVisibility(View.GONE);
            }

            viewHolder.mPostTitle.setText(postInfo.getTitle());
            viewHolder.mPostDesc.setText(postInfo.getContent());

            //设置推荐的内容
            final SuggestIP ip = mPostList.get(position).getSuggestip();
            if (ip != null) {
                if (ip.getIcon() != null) {
                    try {
                        mImageLoader.displayImage(ip.getIcon().getUri(), viewHolder.mIPCover, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                }
                viewHolder.mIPName.setText(ip.getName());
                viewHolder.mIPReleaseDate.setText((IPResourceManager.getIpTypeName(mContext, ip.getType()) + "/"
                        + StringUtil.formatDate(ip.getReleaseDate(), IPResourceConstants.IP_RELEASE_DATE_FORMAT)));
            }

            final SuggestEmoji emoji = mPostList.get(position).getSuggestemoji();
            if (emoji != null) {
                if (emoji.getIcon() != null) {
                    try {
                        mImageLoader.displayImage(emoji.getIcon().getUri(), viewHolder.mEmojiCover, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }

                }
                viewHolder.mEmojiName.setText(emoji.getName());
                viewHolder.mEmojiNum.setText("表情个数：" + emoji.getEmojiNum());
            }

            // 设置分享
            final PostInfo post = postInfo;
            viewHolder.mShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = WowoContants.POST_SHARE_URL + post.getWoid() + "/" + post.getPostid();
                    UmengSocialCenter shareCenter = new UmengSocialCenter(mContext);
                    shareCenter.setShareContent(post.getTitle(), post.getContent(), url,  post.getCover().getUri());
                    shareCenter.openShare(mContext);
                }
            });

            // 设置评论action
            viewHolder.mComment.setText(""  + postInfo.getCommentCount());
            viewHolder.mCommentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转帖子详情的具体楼层？
                    gotoPostDetail(post);
                }
            });
            viewHolder.mPostAuthorArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoPostDetail(post);
                }
            });

            //点击进入帖子内容
            viewHolder.mPostCotentArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoPostDetail(post);
                }
            });

            //点击进入ip详情
            viewHolder.mSuggestIPArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击进入ip详情
                    Bundle b = new Bundle();
                    b.putString("ip_id", ip.getId());
                    b.putBoolean("view_ip", true);
                    Intent intent = new Intent(mContext, IPMoinClipActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(b);
                    mContext.startActivity(intent);
                }
            });

            //点击进入表情专辑
            viewHolder.mSuggestEmojiArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击进入表情详情
                    Bundle b = new Bundle();
                    b.putString("ipid", emoji.getIpid());
                    b.putString("ipname", emoji.getName());
                    Intent intent = new Intent(mContext, EmojiResourceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(b);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private void gotoPostDetail(PostInfo post) {
        MyLog.i("ljc: WoPostAdaptor: post-->woid =" + post.getWoid() + "id =" + post.getPostid() + ", post title =" + post.getTitle());
        Bundle b = new Bundle();
        b.putSerializable(PostDetailActivity.KEY_WO_ID, post.getWoid());
        b.putString(PostDetailActivity.KEY_POST_ID, post.getPostid());
        AppTools.toIntent(mContext, b, PostDetailActivity.class);
    }
    private ListViewHolder getListViewHolder(View convertView) {
        ListViewHolder holder = new ListViewHolder();
        holder.mPostCover = (ImageView) convertView.findViewById(R.id.post_cover);
        holder.mAuthorAvatar = (ImageView) convertView.findViewById(R.id.author_avatar);
        holder.mAuthorName = (TextView) convertView.findViewById(R.id.author_name);
        holder.mAuthorLevel = (TextView) convertView.findViewById(R.id.author_level);
        holder.mPostUpdateAt = (TextView) convertView.findViewById(R.id.post_time);
        holder.mSuggestImage = (ImageView) convertView.findViewById(R.id.post_suggest_image);
        holder.mPicImage = (ImageView) convertView.findViewById(R.id.post_pic_image);
        holder.mEmojiImage = (ImageView) convertView.findViewById(R.id.post_emoji_image);
        holder.mPostTitle = (TextView) convertView.findViewById(R.id.post_title);
        holder.mPostDesc = (TextView) convertView.findViewById(R.id.post_desc);

        //推荐ip
        holder.mIPCover = (ImageView) convertView.findViewById(R.id.wo_ip_cover);
        holder.mIPName = (TextView) convertView.findViewById(R.id.wo_ip_name);
        holder.mIPReleaseDate = (TextView) convertView.findViewById(R.id.wo_ip_release_name);

        holder.mEmojiCover = (ImageView) convertView.findViewById(R.id.wo_ip_emoji_cover);
        holder.mEmojiName = (TextView) convertView.findViewById(R.id.wo_ip_emoji_name);
        holder.mEmojiNum = (TextView) convertView.findViewById(R.id.wo_ip_emoji_num);


        holder.mShare = (ImageView) convertView.findViewById(R.id.wo_post_share);
        holder.mCommentImage = (ImageView) convertView.findViewById(R.id.wo_post_comment_image);
        holder.mComment = (TextView) convertView.findViewById(R.id.wo_post_comment);
        holder.mCommentView = (LinearLayout) convertView.findViewById(R.id.comment);

        holder.mPostAuthorArea = (RelativeLayout) convertView.findViewById(R.id.post_author_area);
        holder.mPostCotentArea = (LinearLayout) convertView.findViewById(R.id.post_content_area);
        holder.mSuggestIPArea = (LinearLayout) convertView.findViewById(R.id.suggest_ip_area);
        holder.mSuggestEmojiArea = (LinearLayout) convertView.findViewById(R.id.suggest_emoji_area);
        return holder;
    }

    public class ListViewHolder {
        public ImageView mPostCover; //帖子封面
        public ImageView mAuthorAvatar; //帖子作者头像
        public TextView mAuthorName; //帖子作者
        public TextView mAuthorLevel; //帖子作者身份，版主等等
        public TextView mPostUpdateAt; //帖子发布时间
        public ImageView mSuggestImage; //帖子是精品贴的图标
        public ImageView mPicImage; //帖子包含图片的图标
        public ImageView mEmojiImage; //帖子包含表情的图标
        public TextView mPostTitle; //帖子标题
        public TextView mPostDesc; //帖子描述

        // 推荐ip
        public ImageView mIPCover; //窝贴推荐的ip封面
        public TextView mIPName; //窝贴推荐ip的名称
        public TextView mIPReleaseDate; //窝贴推荐ip的上映日期
        // 推荐emoji
        public ImageView mEmojiCover; //窝贴推荐的表情封面
        public TextView mEmojiName; //窝贴推荐的表情名称
        public TextView mEmojiNum; //窝贴推荐表情个数

        public ImageView mShare; //分享
        public LinearLayout mCommentView; //评论的view
        public TextView mComment; //帖子评论的个数
        public ImageView mCommentImage; //帖子评论的图标

        public RelativeLayout mPostAuthorArea; //帖子作者的点击区域
        public LinearLayout mPostCotentArea;//帖子内容的点击区域，用户跳转帖子详情
        public LinearLayout mSuggestIPArea;//推荐ip的点击区域
        public LinearLayout mSuggestEmojiArea;//推荐表情的点击区域
    }

    /**
     * IP首页需要用到的窝贴对象，包括帖子基本信息和推荐的IP和表情
     */
//    public static class IPPostInfo {
//        public PostInfo  postInfo;
//        public SuggestIP ip;
//        public SuggestEmoji emoji;
//    }
}

