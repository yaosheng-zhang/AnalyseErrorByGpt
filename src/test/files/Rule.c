#include<stdio.h>
void test1() {
    static const unsigned char t[] = {
    0, 0x1db71064, 0x3b6e20c8, 0x26d930ac, 0x76dc4190, 0x6b6b51f4, 0x4db26158, 0x5005713c,
    /* CRC32 Table */
    0xedb88320, 0xf00f9344, 0xd6d6a3e8, 0xcb61b38c, 0x9b64c2b0, 0x86d3d2d4, 0xa00ae278, 0xbdbdf21c
};

}

void test2() {
    long long number5 = 50l; // Valid: 'l' used as a literal suffix for long long
}

void test3(){
    int a = 0;
    if (a > 0) {
        int b = 1; // 违反规则，无法访问的代码
    }
    return 0;
}

void test4(){
    int a = 010;
    return 0;
}

void test5() {
label_1:
    goto label_1;
label_1:
    return 0;
}