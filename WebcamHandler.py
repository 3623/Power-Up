# import the necessary packages
from threading import Thread
import cv2

### Copied from the imutils library by Adrian Rosebrock

class WebcamHandler:
    def __init__(self, src=0):
        # initialize the video camera stream and read the first frame
        # from the stream
        self.stream = cv2.VideoCapture(src)
        (self.grabbed, self.frame) = self.stream.read()

        # initialize the variable used to indicate if the thread should
        # be stopped
        self.stopped = False

    def start(self):
        # start the thread to read frames from the video stream
        t = Thread(target=self.update, args=())
        t.daemon = True
        t.start()
        return self

    def update(self):
        # keep looping infinitely until the thread is stopped
        while True:
            # if the thread indicator variable is set, stop the thread
            if self.stopped:
                return

            # otherwise, read the next frame from the stream
            (self.grabbed, self.frame) = self.stream.read()
            ### TODO Might want to just grab, maybe??

    def read(self):
        # return the frame most recently read
        return self.frame

    def stop(self):
        # indicate that the thread should be stopped
        self.stopped = True

if __name__ == '__main__':
    video = WebcamHandler(1)
    video.start()
    count = 0
    while True:
        cv2.imshow("WebcamHandler",video.read())
        count += 1
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    print count