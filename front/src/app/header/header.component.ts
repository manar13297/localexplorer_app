import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {UserService} from "../services/user.service";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  explorer: any;

  constructor(private explorerService: UserService) {}

  ngOnInit(): void {
    const explorerId = 2;
    this.explorerService.getExplorerById(explorerId).subscribe({
      next: (explorer) => {
        if (explorer && explorer.username) {
          this.explorer = explorer.username;
        } else {
          console.error('Explorer data is incomplete:', explorer);
        }
      },
      error: (e) => console.error(e)
    });
  }

}
