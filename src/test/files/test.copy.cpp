// 重构后的代码如下：
void test(){
    int a = 0;
    int b = 0; // 声明变量b并初始化为0
    if (a > 0) {
        b = 1; // 将变量b的赋值放到if语句里面
    }
    // 删除无用的return语句
}


