# NDK note:

If you are getting `omp.h` not found, you need to compile with GCC, not clang. This can be done:

`cd apps-android/learner/app/src/main`

`export NDK_TOOLCHAIN_VERSION=4.9`

`~/Library/Android/sdk/ndk-bundle/ndk-build V=1`
