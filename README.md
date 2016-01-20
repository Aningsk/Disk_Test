#Disk Test (Version 1.3.3) 
This Android APP is use to test your device's SD/eMMC.   
Developed by Aningsk, and comply with Apache License Version 2.0

##Description
This APP can calculate the speed of read/write in SD/eMMC, and save the result in a file.
Sometime we shall change some code to make sure it can run on another device. 
It isn't enough smart now.

I use ADT(eclipse) to develop this project. I cannot get Android Studio.  
And run it on Android-4.4.2 and Android-4.4.3 (I don't test it on other Android versions.)

* Android Developer Tools  
    Build:v22.6.2-1085508  

That is very very difficult to get develop tools or support libraries, since Google had been **GFWed**... 
I'm a Chinese newbie programmer without any VPN @\_@

##Version Mark 
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
