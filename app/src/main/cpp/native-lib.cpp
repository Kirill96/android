#include <jni.h>
#include <string>


extern "C"
jstring
Java_com_example_kirill_lab_134_ListSongsActivity_calkduration(JNIEnv *env, jobject instance,
                                                             jdoubleArray mas_) {
    jdouble *mas = env->GetDoubleArrayElements(mas_, NULL);

    double res = 0;
    for(int i= 0; i < 7; i++){
        res += mas[i];
    }

    env->ReleaseDoubleArrayElements(mas_, mas, 0);

    return env->NewStringUTF("");
}

extern "C"
jstring
Java_com_example_kirill_lab_134_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
