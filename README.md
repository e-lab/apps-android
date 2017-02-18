# Android Torch applications

This repo builds some application for Android to use [Torch7](http://torch.ch/) trained neural networks.
It uses [thnets](https://github.com/mvitez/thnets) as a backend, and does not require to compile the full Torch7 on Android.
Thus makes your life a lot easier!



# compile linked libs:

cd ~/FWDNXT/git/AIPerLife/app/src/main

export NDK_TOOLCHAIN_VERSION=4.9

~/Library/Android/sdk/ndk-bundle/ndk-build V=1

