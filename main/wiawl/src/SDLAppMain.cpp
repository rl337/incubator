#include "SDLAppMain.h"

using namespace std;

SDLAppMain::SDLAppMain() {
    _running = true;

    _display = NULL;
}

void SDLAppMain::log(string message) {
    log(message.c_str());
}

void SDLAppMain::log(const char *message) {
    log((char *) message);
}

void SDLAppMain::log(char *message) {
    if (message == NULL) {
        return;
    }

    time_t rawtime;
    struct tm * timeinfo;

    time ( &rawtime );
    timeinfo = localtime ( &rawtime );
    cerr << asctime(timeinfo) << " " << message << endl;
}

bool SDLAppMain::initialize() {
    log("Initializing...");
    if(SDL_Init(SDL_INIT_EVERYTHING) < 0) {
        return false;
    }
 
    if((_display = SDL_SetVideoMode(640, 480, 32, SDL_HWSURFACE | SDL_DOUBLEBUF)) == NULL) {
        return false;
    }
    log("Opened Window...");

    if (IMG_Init(IMG_INIT_PNG) != IMG_INIT_PNG) {
        log(string("IMG_Init Failed: ") + IMG_GetError());
        return false;
    }

    _level = new Level();
    _level->initialize();

    _direction = 0;
 
    return true;
}

void SDLAppMain::cleanup() {
    log("Cleaning up...");
    _level->cleanup();
    log("Disposing of display...");
    SDL_FreeSurface(_display);
    IMG_Quit();
    SDL_Quit();
    log("cleanup complete...");
}

void SDLAppMain::doEvents(SDL_Event* Event) {
    if(Event->type == SDL_QUIT) {
        _running = false;
    }

    if(Event->type == SDL_KEYDOWN) {
        switch(Event->key.keysym.sym) {
           case SDLK_LEFT: _direction -= 5; break;
           case SDLK_RIGHT: _direction += 5; break;
           default: break;
        }
    }
 
    if(Event->type == SDL_KEYUP) {
        switch(Event->key.keysym.sym) {
           case SDLK_LEFT: _direction += 5; break;
           case SDLK_RIGHT: _direction -= 5; break;
           default: break;
        }
    }


}

void SDLAppMain::loop() {
    _level->slideX(_direction);
}

void SDLAppMain::render() {
    _level->render(_display, 640, 480, 0, 0);
    SDL_Flip(_display);
}
 
int SDLAppMain::execute() {
    if(initialize() == false) {
        return -1;
    }
 
    SDL_Event Event;
 
    while(_running) {
        while(SDL_PollEvent(&Event)) {
            doEvents(&Event);
        }
 
        loop();
        render();
    }
 
    cleanup();
 
    return 0;
}
 
