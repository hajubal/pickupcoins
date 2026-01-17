import { Module } from '@nestjs/common';
import { SavedPointController } from './saved-point.controller';
import { SavedPointService } from './saved-point.service';

@Module({
  controllers: [SavedPointController],
  providers: [SavedPointService],
  exports: [SavedPointService],
})
export class SavedPointModule {}
