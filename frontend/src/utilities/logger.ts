export class Logger {
     private readonly _category: string;

     constructor(category: string) {
          this._category = category;
     }

     debug(message: string): void {
          console.log(`%c[%cDEBUG%c]/[%c${this._category}%c]%c ${message}`, "color: grey;", "color: purple", "color: grey", "color: cyan", "color: grey", "color: white");
     }

     info(message: string): void {
          console.info(`%c[%cDEBUG%c]/[%c${this._category}%c]%c ${message}`, "color: grey;", "color: lightblue", "color: grey", "color: cyan", "color: grey", "color: white");
     }

     warn(message: string): void {
          console.warn(`%c[%cWARN%c]/[%c${this._category}%c]%c ${message}`, "color: grey;", "color: yellow", "color: grey", "color: cyan", "color: grey", "color: white");
     }

     error(message: string): void {
          console.error(`%c[%cERROR%c]/[%c${this._category}%c]%c ${message}`, "color: grey;", "color: red", "color: grey", "color: cyan", "color: grey", "color: white");
     }
}