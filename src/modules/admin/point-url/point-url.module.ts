import { Module } from '@nestjs/common';
import { PointUrlController } from './point-url.controller';
import { PointUrlService } from './point-url.service';

@Module({
  controllers: [PointUrlController],
  providers: [PointUrlService],
  exports: [PointUrlService],
})
export class PointUrlModule {}
