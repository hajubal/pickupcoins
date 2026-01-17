import { Module } from '@nestjs/common';
import { CookieController } from './cookie.controller';
import { CookieService } from './cookie.service';

@Module({
  controllers: [CookieController],
  providers: [CookieService],
  exports: [CookieService],
})
export class CookieModule {}
