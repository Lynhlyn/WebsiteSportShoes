
<template>
  <div class="image-upload">
    <!-- File Input -->
    <div class="mb-3">
      <label class="form-label">{{ label }}</label>
      <input
        type="file"
        class="form-control"
        accept="image/*"
        multiple
        @change="handleFileChange"
        ref="fileInput"
      />
      <small v-if="error" class="text-danger">{{ error }}</small>
      <small class="text-muted d-block" v-if="showEditHint">
        Không bắt buộc. Bỏ trống nếu muốn giữ ảnh cũ.
      </small>
    </div>

    <!-- Current Images -->
    <div v-if="currentImages.length > 0" class="mb-3">
      <label class="form-label">Ảnh hiện tại:</label>
      <div class="image-grid">
        <div v-for="(image, index) in currentImages" :key="'current-' + index" class="image-item">
          <div class="image-container">
            <img :src="image" :alt="'Current image ' + (index + 1)" class="preview-image" />
            <button 
              type="button" 
              class="btn btn-danger btn-sm remove-button"
              @click="removeCurrentImage(index)"
            >
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- New Image Previews -->
    <div v-if="previews.length > 0" class="mb-3">
      <label class="form-label">{{ showEditHint ? 'Ảnh mới:' : 'Ảnh đã chọn:' }}</label>
      <div class="image-grid">
        <div v-for="(preview, index) in previews" :key="'preview-' + index" class="image-item">
          <div class="image-container">
            <img :src="preview" :alt="'Preview ' + (index + 1)" class="preview-image" />
            <button 
              type="button" 
              class="btn btn-danger btn-sm remove-button"
              @click="removeNewImage(index)"
            >
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  label: {
    type: String,
    default: 'Hình ảnh'
  },
  error: {
    type: String,
    default: ''
  },
  showEditHint: {
    type: Boolean,
    default: false
  },
  value: {
    type: Array,
    default: () => []
  },
  currentImages: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:value', 'update:currentImages']);

const fileInput = ref(null);
const files = ref([]);
const previews = ref([]);

// Watch for value changes from parent
watch(() => props.value, (newValue) => {
  if (newValue.length === 0) {
    files.value = [];
    previews.value = [];
  }
}, { deep: true });

const validateFile = (file) => {
  // Check file type
  if (!file.type.startsWith('image/')) {
    return 'Chỉ chấp nhận file ảnh';
  }

  // Check file size (max 5MB)
  const maxSize = 5 * 1024 * 1024; // 5MB in bytes
  if (file.size > maxSize) {
    return 'Kích thước file không được vượt quá 5MB';
  }

  return null;
};

const handleFileChange = (event) => {
  const newFiles = Array.from(event.target.files);
  let hasError = false;

  // Validate each file
  for (const file of newFiles) {
    const error = validateFile(file);
    if (error) {
      alert(error);
      hasError = true;
      break;
    }
  }

  if (!hasError) {
    files.value = [...files.value, ...newFiles];
    
    newFiles.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        previews.value.push(e.target.result);
      };
      reader.readAsDataURL(file);
    });

    emit('update:value', files.value);
  }

  // Reset file input
  event.target.value = '';
};

const removeNewImage = (index) => {
  previews.value.splice(index, 1);
  files.value.splice(index, 1);
  emit('update:value', files.value);
};

const removeCurrentImage = (index) => {
  const updatedImages = [...props.currentImages];
  updatedImages.splice(index, 1);
  emit('update:currentImages', updatedImages);
};

const reset = () => {
  files.value = [];
  previews.value = [];
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

// Expose reset method
defineExpose({
  reset
});
</script>

<style scoped>
.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-button {
  position: absolute;
  top: 0.25rem;
  right: 0.25rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  z-index: 1;
  opacity: 0.9;
}

.remove-button:hover {
  opacity: 1;
}

.form-label {
  font-weight: 500;
}

.text-danger {
  color: #dc3545;
}

.text-muted {
  color: #6c757d;
}
</style>