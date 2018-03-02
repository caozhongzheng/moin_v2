package com.moinapp.wuliao.modules.wowo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.modules.ipresource.EmojiUtils;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/9.
 */
public class CommentsAdapter extends BaseAdapter {
    private ILogger MyLog = LoggerFactory.getLogger("ca");

    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;

    Handler mHandler;
    private List<CommentInfo> mCommentList = new ArrayList<>();
    private int mPicMaxWidth, mEmjMinWidth;

    public CommentsAdapter(Context context, Handler handler) {
        mContext = context;
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        MyLog.i("scrWidth="+ DisplayUtil.getDisplayWidth(mContext)+ ", margin="+mContext.getResources().getDimensionPixelSize(R.dimen.margin_normal)*2);
        mPicMaxWidth = DisplayUtil.getDisplayWidth(mContext) - mContext.getResources().getDimensionPixelSize(R.dimen.margin_normal)*2;
        mEmjMinWidth = mContext.getResources().getDimensionPixelSize(R.dimen.emj_min_width);
        
        this.mHandler = handler;
        mCommentList = new ArrayList<>();
    }

    public List<CommentInfo> getCommentList() {
        return mCommentList;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mCommentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.post_detail_comment_list_item, null);
            viewHolder = getListViewHolder(view);
            view.setTag(viewHolder);
//            MyLog.i("null, inflate view @" + i);
        } else {
//            MyLog.i("not null, reuse view @" + i);
            viewHolder = (ViewHolder) view.getTag();
        }

//        MyLog.i("showing "+i+"@" + getCount());
        setContentView(viewHolder, i);

        return view;
    }

    private ViewHolder getListViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.avatar = (ImageView) convertView.findViewById(R.id.author_avatar);
        holder.emoji = (ImageView) convertView.findViewById(R.id.post_emj);
//        holder.photo_rl = (FrameLayout) convertView.findViewById(R.id.ry_post_images);
//        holder.photo_long = (TextView) convertView.findViewById(R.id.post_images_longpic);
        holder.photo = (ImageView) convertView.findViewById(R.id.post_images);
        holder.nickname = (TextView) convertView.findViewById(R.id.author_name);
        holder.time = (TextView) convertView.findViewById(R.id.post_time);
        holder.level = (TextView) convertView.findViewById(R.id.author_level);
        holder.floor = (TextView) convertView.findViewById(R.id.floor);
        holder.content = (TextView) convertView.findViewById(R.id.post_desc);
        holder.ly_comments = (LinearLayout) convertView.findViewById(R.id.ly_comments);
        holder.ly_referral = (LinearLayout) convertView.findViewById(R.id.ly_referral);
        holder.referral_floor = (TextView) convertView.findViewById(R.id.referral_floor);
        holder.referral_meta = (TextView) convertView.findViewById(R.id.referral_meta);

        return holder;
    }

    private void setContentView(final ViewHolder viewHolder, final int position) {

        final CommentInfo commentInfo = mCommentList.get(position);
        if(commentInfo.getAuthor() != null) {
            try {
                imageLoader.displayImage(commentInfo.getAuthor().getAvatar().getUri(), viewHolder.avatar, BitmapUtil.getImageLoaderOption());
            } catch (NullPointerException e) {
                MyLog.e(e);
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
        } else {
            viewHolder.avatar.setImageResource(R.drawable.icon);
        }

        MyLog.i("showing------------------------------------- "+position+"/" + getCount() + ", cid="+commentInfo.getCid());

        if(commentInfo.getEmoji() != null) {
            final String emjUrl = commentInfo.getEmoji().getPicture().getUri();
            if(!StringUtil.isNullOrEmpty(emjUrl)) {
                final String emjPath = EmojiUtils.getEmjPath(commentInfo.getEmoji());
//                MyLog.i(position + " commentInfo.getEmoji() url=" + emjUrl);
//                MyLog.i(position + " commentInfo.getEmoji() path=" + emjPath);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean down = HttpUtil.download(emjUrl, emjPath);
                        if (down) {
//                    MyLog.i("commentInfo.getEmoji() show gif=" + emjPath);
//                            GifUtils.displayGif(viewHolder.emoji, new File(emjPath));
//                            map.put(position, viewHolder.emoji);

                            try {
                                ViewGroup.LayoutParams params = viewHolder.emoji.getLayoutParams();
                                // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
//                                final BitmapFactory.Options options = new BitmapFactory.Options();
//                                options.inJustDecodeBounds = true;
//                                Bitmap bmp = BitmapFactory.decodeFile(emjPath, options);
                                int gifWidth = commentInfo.getEmoji().getPicture().getWidth();
                                int gifHeight = commentInfo.getEmoji().getPicture().getHeight();

                                int[] p = EmojiUtils.getScaleEmjHeight(gifWidth, gifHeight, mEmjMinWidth);
                                params.width = p[0];
                                params.height = p[1];
                                MyLog.i("显示第 "+commentInfo.getCid()+" 楼的表情 img.W*H = [" + gifWidth + "*" + gifHeight + "]" + ", new height=[" + params.height + "]");
                                viewHolder.emoji.setLayoutParams(params);
                                GifUtils.getInstance(2, GifUtils.Type.FIFO).loadImage(emjPath, viewHolder.emoji);
                            } catch (Exception e) {
                                MyLog.e(e);
                            }

                            viewHolder.emoji.setVisibility(View.VISIBLE);
                            viewHolder.emoji.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mHandler != null) {
                                        Message msg = new Message();
                                        msg.what = 0x36;
                                        msg.arg1 = position + 1;
                                        msg.obj = emjUrl;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            });
                        } else {
                            viewHolder.emoji.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                viewHolder.emoji.setVisibility(View.GONE);
            }

        } else {
            viewHolder.emoji.setVisibility(View.GONE);
        }

        try {
            if(commentInfo.getImages() != null && commentInfo.getImages().size() > 0) {

                BaseImage image = commentInfo.getImages().get(0);
                try {
                    imageLoader.displayImage(image.getUri(), viewHolder.photo, BitmapUtil.getImageLoaderOption());
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }

                ViewGroup.LayoutParams params = viewHolder.photo.getLayoutParams();
                int[] newwh = EmojiUtils.getScaleHeight(image.getWidth(), image.getHeight(), mPicMaxWidth);
                params.width = newwh[0];
                params.height = newwh[1];
                if(newwh[1] <= 0) {
                    Bitmap bmp = imageLoader.loadImageSync(image.getUri());
                    if(bmp != null) {
                        int[] params2 = EmojiUtils.getScaleHeight(bmp.getWidth(), bmp.getHeight(), mPicMaxWidth);
                        MyLog.i("loadImageSync w*h" + bmp.getWidth() + " * " + bmp.getHeight() + " , new height=[" + params2[1] + "]");
                        params.width = params2[0];
                        params.height = params2[1];
                    }
                }
//                float tooHeigh = (float) params.height / (float) params.width;
//                if(tooHeigh > 4) {
//                    viewHolder.photo_long.setVisibility(View.VISIBLE);
//                } else {
//                    viewHolder.photo_long.setVisibility(View.GONE);
//                }

                MyLog.i("updateCommentsAdapter mPicMaxWidth=" + mPicMaxWidth);
                MyLog.i(commentInfo.getCid()+"楼 updateCommentsAdapter img.W*H = [" + image.getWidth() + "*" + image.getHeight() + "]" + ", new height=[" + params.height + "]");
                viewHolder.photo.setLayoutParams(params);

                viewHolder.photo.setVisibility(View.VISIBLE);
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mHandler != null) {
                            Message msg = new Message();
                            msg.what = 0x24;
                            msg.obj = commentInfo.getImages().get(0).getUri();
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            } else {
                viewHolder.photo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MyLog.e(e);
            viewHolder.photo.setVisibility(View.GONE);
        }


        viewHolder.nickname.setText(commentInfo.getAuthor().getNickname());
        viewHolder.time.setText(StringUtil.humanDate(commentInfo.getUpdatedAt(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
        if (commentInfo.getAuthor().getLevel() == 10000){
            viewHolder.level.setText(mContext.getString(R.string.banzhu));
        }else{
            viewHolder.level.setVisibility(View.INVISIBLE);
        }
        //viewHolder.level.setText(getLevel(commentInfo.getAuthor().getLevel()));
        viewHolder.floor.setText(String.format(mContext.getResources().getString(R.string.comment_floor), commentInfo.getCid()));

        if(StringUtil.isNullOrEmpty(commentInfo.getContent())){
            viewHolder.content.setVisibility(View.GONE);
        }else{
            viewHolder.content.setVisibility(View.VISIBLE);
            viewHolder.content.setText(commentInfo.getContent());
        }
        viewHolder.ly_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x22;
                    msg.obj = commentInfo;
                    mHandler.sendMessage(msg);
                }
            }
        });

        if(commentInfo.getReferral() <= 0) {
            viewHolder.ly_referral.setVisibility(View.GONE);
        } else {
            viewHolder.ly_referral.setVisibility(View.VISIBLE);
            viewHolder.referral_floor.setText(String.format(mContext.getResources().getString(R.string.reply_referral_floor), commentInfo.getReferral()));
            viewHolder.referral_meta.setText(StringUtil.nullToEmpty(commentInfo.getMeta()));
        }
    }


    class ViewHolder {
        public ImageView avatar;
        public ImageView emoji;
//        public FrameLayout photo_rl;
        public ImageView photo;
//        public TextView photo_long;
        public TextView nickname;
        public TextView time;
        public TextView level;
        public TextView floor;
        public TextView content;
        public LinearLayout ly_comments;
        public LinearLayout ly_referral;
        public TextView referral_floor;
        public TextView referral_meta;
    }

}
