package com.moinapp.wuliao.modules.login;

/**
 * Created by liujiancheng on 15/7/14.
 */
public class LoginSuccJump {
    public static class JumpIPDetailEvent {

    }

    public static class JumpEmojiResourceEvent {
        private int index;

        public JumpEmojiResourceEvent(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }

    public static class JumpWoPostAttentionEvent {
        private int column;

        public JumpWoPostAttentionEvent(int column) {
            this.column = column;
        }

        public int getColumn() {
            return this.column;
        }
    }

    public static class LoginSuccessEvent {

    }

//    public static class JumpFragmentWowoEvent {
//
//    }
//
    public static class JumpWoRuleEvent {

    }

    public static class JumpWoPostNewPostEvent {

    }
//
//    public static class JumpPostCommentEvent {
//
//    }
//
    public static class JumpPostReplyEvent {

    }
}
