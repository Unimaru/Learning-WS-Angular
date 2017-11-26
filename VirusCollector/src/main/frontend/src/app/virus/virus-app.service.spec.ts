import { TestBed, inject } from '@angular/core/testing';

import { VirusAppService } from './virus-app.service';

describe('VirusAppService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [VirusAppService]
    });
  });

  it('should be created', inject([VirusAppService], (service: VirusAppService) => {
    expect(service).toBeTruthy();
  }));
});
