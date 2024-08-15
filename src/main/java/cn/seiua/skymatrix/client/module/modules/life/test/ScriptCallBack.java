package cn.seiua.skymatrix.client.module.modules.life.test;

public interface ScriptCallBack {
    public static ScriptCallBack defaultCallBack=new defaultCallBack();
    /**
     * @param root 脚本
     * @param index 当前脚本的索引
     * @param length 脚本的长度
     * @return 返回true表示继续执行，返回false表示停止执行
     */
    boolean next(Script root,int index,int length);

    /**
     *
     * @param root root脚本
     */
    void finish(Script root);
     class defaultCallBack implements ScriptCallBack{

        @Override
        public boolean next(Script root, int index, int length) {
            return true;
        }

        @Override
        public void finish(Script root) {

        }
    }
}
