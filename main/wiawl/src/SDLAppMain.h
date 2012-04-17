#ifndef _FLUIDSMAIN_H_
#define _FLUIDSMAIN_H_
 
#include <string>
#include <time.h>
#include <iostream>
#include <SDL/SDL.h>
#include <SDL_image/SDL_image.h>

#include "Level.h"
 
class SDLAppMain {
    private:
        bool _running;
        Level *_level;
        SDL_Surface *_display;
 
        int _direction;

    protected:
        void log(std::string message);
        void log(const char *message);
        void log(char *message);


    public:
        SDLAppMain();
 
        int execute();
 
        bool initialize();
        void doEvents(SDL_Event* Event);
        void loop();
        void render();
        void cleanup();
};
 
#endif
