import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../utils/auth/auth.service';
import { GoogleSsoDirective } from '../../utils/google-directive/google-sso.directive';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatIconModule,
    GoogleSsoDirective,
  ],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css',
})
export class SignUpComponent {
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  passwordFormControl = new FormControl('', [
    Validators.required,
    Validators.pattern('(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}'),
  ]);

  private formGroup!: FormGroup;

  public hide!: boolean;

  ngOnInit() {
    this.hide = true;
    this.formGroup = new FormGroup({
      email: this.emailFormControl,
      password: this.passwordFormControl,
    });
  }

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (this.formGroup.valid) {
      // Register new user
      const email = this.emailFormControl.getRawValue();
      const password = this.passwordFormControl.getRawValue();

      if (email !== null && password !== null) {
        this.authService
          .signUpWithEmailAndPassword({ email, password })
          .subscribe({
            next: (response) => {
              console.log('Sign up successful');
              this.router.navigate(['/dashboard']);
            },
            error: (error) => {
              console.error('Error signing up', error);
            },
          });
      }
    }
  }
}
