import { Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {ExploreComponent} from "./explore/explore.component";



export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'explore', component: ExploreComponent}
];
