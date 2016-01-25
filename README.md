#Disk Test (Version 1.6.1) 
This Android APP is use to test your device's SD/eMMC.   
Developed by Aningsk, and comply with Apache License Version 2.0

##Description
This APP can calculate the speed of read/write in SD/eMMC, and save the result in a file.
User can select where they want to take test on (Internal or External Disk).
As a whole test will spend too long time, sometimes I only take a bit of test (16K and 32K files). 
If you want to test other kinds size, please change the QUANTITY or testsize in TestService.java 
and make sure that your device has enough big disk.   
When the APP takes a test, it will create TestFile.txt with random data, and copy it as 
TempFile.txt . APP can check the MD5 of these two file are the same value or not, and get the 
speed of read/write. And repeat 5 times. User can see the result by clicking "Result" button.

I use ADT(eclipse) to develop this project. I cannot get Android Studio.  
And run it on Android-4.4.2 (I don't test it on other Android versions.), 
I considered much older Android such as 4.2.2, but Android-4.4 is my target device.

* Android Developer Tools  
    Build:v22.6.2-1085508  

That is very very difficult to get develop tools or support libraries, since Google had been **GFWed**... 
I'm a Chinese newbie programmer without any VPN @\_@

***
##Version Mark 
* v1.6.1 (2016-01-25)   
    Fixed some issue when device without SD card:   
    1 ShowView don't show correct information. FIXED   
    2 RadioGroup can select one that shouldn't be selected. FIXED  
* v1.6 (2016-01-22)   
    Support that take test on internal disk or external disk.
* v1.5.1 (2016-01-22)   
    It can show the TestResult.txt on UI.   
    Unitize some values by DiskSizeApplication.
* v1.5 (2016-01-21)   
    Now the APP can read/write in internal disk, as the test files are in the APP private path.  
    So it does not need uid.system. 
* v1.4 (2016-01-20)   
    If something is getting wrong such as IOEception, user can know that.   
    DiskSize means /data size, not /sdcard. If necessary I can change it.   
    All file created by the APP will be saved in "DiskTest" folder.
* v1.3.3 (2016-01-20)
    BUG:   
    If user stops a running TestService, the last average speed will get wrong.   
    Fixed.
* v1.3.2 (2016-01-19)   
    Decimal format as 0.000000 in the TestResult.txt   
    Adjust some strings.
* v1.3.1 (2016-01-19)   
    Support Simplified Chinese Language.  
* v1.3 (2016-01-19)   
    The APP can show RAM size, eMMC size and partitions information.
* v1.2 (2016-01-18)   
    The read/write speed is more accurate than before!  
    If there is some fail in test, APP can tell us.
* v1.1 (2016-01-18)   
    Use MD5 checksum, and change read/write API to FileReader/FileWriter.  
    The data written in file is random.
* v1.0 (2016-01-15)  
    I use my old project to init the git repo.  
    Some functions were too stupid, I'll change them.

***
## License

    Copyright 2016 Aningsk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
