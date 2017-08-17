
interface ICropPlugin{
    promise(string: string, option:any): Promise<any>;
}
declare module "CropPlugin" {
    export = imageResizer;
}

declare var imageResizer : ICropPlugin;