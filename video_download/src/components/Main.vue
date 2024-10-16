<template>

<div class="common-layout">
    <el-container>
      <el-aside width="200px">Aside</el-aside>
      <el-main>Main</el-main>
    </el-container>
  </div>



</template>

<script setup lang="ts">
import { ref } from 'vue'
import { genFileId } from 'element-plus'
import type { UploadInstance, UploadProps, UploadRawFile } from 'element-plus'

const upload = ref<UploadInstance>()

const handleExceed: UploadProps['onExceed'] = (files) => {
    upload.value!.clearFiles()
    const file = files[0] as UploadRawFile
    file.uid = genFileId()
    upload.value!.handleStart(file)
}

const submitUpload = () => {
    upload.value!.submit()
}
</script>

<style>
.common-layout {
    background-color: aqua;
    height: 1080px;
    width: 1920px;
    left: -1
}
.common-layout > el-container > el-side {
    background-color: aqua;
}
el-main{
    background-color: bisque;
}
</style>