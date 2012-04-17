#ifndef _LEVEL_H_
#define _LEVEL_H_
 
#include <string>
#include <iostream>
#include <SDL/SDL.h>
#include <SDL_image/SDL_image.h>

typedef struct {
    public:
        SDL_Surface *bitmap;
        int x, y;
} LevelObject;
 
class Level {
    private:
        static const int _background_count;
        static const char *_background_images[];

        static const int _cloud_count;
        static const char *_cloud_images[];

        static const int _foreground_count;
        static const char *_foreground_images[];

        static const int _horizon_count;
        static const char *_horizon_images[];

        static const int _middleground_count;
        static const char *_middleground_images[];

        SDL_Surface **_backgrounds;
        LevelObject *_background_objects;
        int _background_objects_count;

        SDL_Surface **_clouds;
        LevelObject *_cloud_objects;
        int _cloud_objects_count;

        SDL_Surface **_foregrounds;
        LevelObject *_foreground_objects;
        int _foreground_objects_count;

        SDL_Surface **_horizons;
        LevelObject *_horizon_objects;
        int _horizon_objects_count;

        SDL_Surface **_middlegrounds;
        LevelObject *_middleground_objects;
        int _middleground_objects_count;

        int _width;
        int _height;

        int _posX;

    protected:
        SDL_Surface **load_image_series(std::string dir, int count, const char *filenames[]);
        void free_image_series(SDL_Surface **images, int count);
        void generate_level(int*, LevelObject**, SDL_Surface**, int, int);

    public:
        Level();

        void slideX(int val);
 
        bool initialize();
        void render(SDL_Surface *display, int x, int y, int width, int height);
        void cleanup();
};
 
#endif
