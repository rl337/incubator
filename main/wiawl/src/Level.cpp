#include "Level.h"

using namespace std;

const int Level::_background_count = 4;
const char *Level::_background_images[] = { "background-00.png", "background-01.png", "background-02.png", "background-03.png" };

const int Level::_cloud_count = 3;
const char *Level::_cloud_images[] = { "clouds-00.png","clouds-01.png","clouds-02.png" };

const int Level::_foreground_count = 3;
const char *Level::_foreground_images[] = {"foreground-0.png","foreground-1.png","foreground-2.png"};

const int Level::_horizon_count = 1;
const char *Level::_horizon_images[] = { "horizon.png" };

const int Level::_middleground_count = 5;
const char *Level::_middleground_images[] = { "middleground-00.png","middleground-01.png","middleground-02.png","middleground-03.png","middleground-04.png" };

Level::Level() {

}

void Level::slideX(int val) {
    _posX += val;
}

SDL_Surface  **Level::load_image_series(string dir, int count, const char *filenames[]) {
    SDL_Surface **result = new SDL_Surface*[count];
    for(int i = 0; i < count; i++) {
        string filename = dir + filenames[i];
        result[i] = IMG_Load(filename.c_str());
    }
    return result;
}

void Level::free_image_series(SDL_Surface **images, int count) {
    for(int i = 0; i < count; i++) {
        SDL_FreeSurface(images[i]);
    }

    delete images;
}

void Level::generate_level(int *count, LevelObject **instances, SDL_Surface **bitmaps, int bitmap_count, int baseHeight) {
    *count = 10;
    (*instances) = new LevelObject[*count];
    int current_x = 0;
    for(int i = 0; i < *count; i++) {
        LevelObject *instance = &(*instances)[i];
        SDL_Surface *bitmap = bitmaps[i % bitmap_count];

        instance->bitmap = bitmap;
        instance->x = current_x;
        instance->y = _height - baseHeight - bitmap->h;

        current_x += bitmap->w;
    }
}

bool Level::initialize() {
    string directory = "Contents/Resources/Images/";
    _backgrounds = load_image_series(directory, _background_count, _background_images);
    _clouds = load_image_series(directory, _cloud_count, _cloud_images);
    _middlegrounds = load_image_series(directory, _middleground_count, _middleground_images);
    _horizons = load_image_series(directory, _horizon_count, _horizon_images);
    _foregrounds = load_image_series(directory, _foreground_count, _foreground_images);

    _width = 4096;
    _height = 480;

    _posX = _width / 2;

    generate_level(&_background_objects_count, &_background_objects, _backgrounds, _background_count, _height / 2);
    generate_level(&_cloud_objects_count, &_cloud_objects, _clouds, _cloud_count, _height * 4 / 5);
    generate_level(&_horizon_objects_count, &_horizon_objects, _horizons, _horizon_count, _height * 3 / 5);
    generate_level(&_middleground_objects_count, &_middleground_objects, _middlegrounds, _middleground_count, _height * 1 / 3);
    generate_level(&_foreground_objects_count, &_foreground_objects, _foregrounds, _foreground_count, -25);

    return true;
}

void Level::cleanup() {
    free(_background_objects);
    free(_cloud_objects);
    free(_foreground_objects);
    free(_horizon_objects);
    free(_middleground_objects);

    free_image_series(_backgrounds, _background_count);
    free_image_series(_clouds, _cloud_count);
    free_image_series(_middlegrounds, _middleground_count);
    free_image_series(_horizons, _horizon_count);
    free_image_series(_foregrounds, _foreground_count);
}

void render_layer(SDL_Surface *display, int objCount, LevelObject *objects, int offset, int width, int height, int x, int y) {

    int lowerBound = offset - width / 2;
    int upperBound = offset + width / 2;

    SDL_Rect DestR;
    SDL_Rect SrcR;
    SrcR.y = 0;
    for(int i = 0; i < objCount; i++) {
        int left = objects[i].x;
        if (left > upperBound) continue;

        SrcR.x = 0;
        SrcR.y = 0;
        if (left < lowerBound) SrcR.x += (lowerBound - left);

        int right = objects[i].x + objects[i].bitmap->w;
        if (right < lowerBound) continue;

        SrcR.w = objects[i].bitmap->w;
        SrcR.h = objects[i].bitmap->h;
        if (right > upperBound) SrcR.w -= (right - upperBound);

        DestR.x = left < lowerBound ? 0 : (left - lowerBound);
        DestR.y = objects[i].y;
        
        SDL_BlitSurface(objects[i].bitmap, &SrcR, display, &DestR);
    }
}

void Level::render(SDL_Surface *display, int width, int height, int x, int y) {
    SDL_Rect sky;
    SDL_Rect ground;

    sky.x = 0;
    sky.y = 0;
    sky.w = display->w;
    sky.h = display->h*2/5;

    ground.x = 0;
    ground.y = sky.h;
    ground.w = display->w;
    ground.h = display->h - sky.h;

    SDL_FillRect(display, &sky, SDL_MapRGB(display->format, 0xe8, 0xc2, 0xbd));

    int foregroundOffset = _posX * 4 / 3;
    int middlegroundOffset = _posX;
    int backgroundOffset = _posX * 2 / 5;
    int horizonOffset = _posX / 2;
    int cloudOffset = _posX / 3;

    render_layer(display, _cloud_objects_count, _cloud_objects, cloudOffset, display->w, display->h, 0, 0);
    render_layer(display, _background_objects_count, _background_objects, backgroundOffset, display->w, display->h, 0, 0);

    render_layer(display, _horizon_objects_count, _horizon_objects, horizonOffset, display->w, display->h, 0, 0);
    SDL_FillRect(display, &ground, SDL_MapRGB(display->format, 0x94, 0xa0, 0x95));

    render_layer(display, _middleground_objects_count, _middleground_objects, middlegroundOffset, display->w, display->h, 0, 0);
    render_layer(display, _foreground_objects_count, _foreground_objects, foregroundOffset, display->w, display->h, 0, 0);
}
