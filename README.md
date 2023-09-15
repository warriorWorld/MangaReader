As the name suggested, it is an application for users to read mangas(in English), I developed this for myself to learn English, and it actually worked. Because of this, I expanded my vocabulary a lot.
This app has a translation feature.

![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss1.jpg) ![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss2.jpg) ![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss6.jpg)

##### Technical detail(if you are not interested, just ignore this part)
This app included a download feature, I used a thread pool to solve this. First, I would get all the image links, and then I would use a thread pool to execute the actual download threads. At every step, I would save its status, this guaranteed that if the process was interrupted, the app could still download the manga from the last point.

![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss3.jpg) ![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss4.jpg) ![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss5.jpg) 
![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss7.jpg) ![ss](https://github.com/warriorWorld/MangaReader/blob/master/app/screenshot/ss8.jpg)
