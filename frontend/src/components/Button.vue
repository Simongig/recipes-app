<template>
  <button v-if="isInputType === false" :class="buttonClasses" :style="buttonStyles">
    <span v-if="icon_placement == 'left'">
      <ion-icon class="hydrated {{buttonClasses}}" name="{{icon_name}}"></ion-icon>
      {{ text }}
    </span>
    <span v-else>
      {{ text }}
      <ion-icon name="{{icon_name}}"></ion-icon>
    </span>
  </button>

  <input v-else :type="inputType" :value="text" :class="buttonClasses" :style="buttonStyles" />
</template>

<script>
export default {
  props: {
    text: {
      type: String,
      default: '',
    },
    bgColor: {
      type: String,
      default: '#f5f5f5',
    },
    fullWidth: {
      type: Boolean,
      default: false,
    },
    icon: {
      type: Boolean,
      default: false,
    },
    iconPosition: {
      type: String,
      default: 'left', // other options: 'right'
    },
    isInputType: {
      type: Boolean,
      default: false,
    },
    inputType: {
      type: String,
      default: 'button', // other options could be 'submit', etc.
    },
  },
  computed: {
    buttonStyles() {
      return {
        backgroundColor: this.bgColor,
        width: this.fullWidth ? '100%' : 'auto',
      }
    },
    buttonClasses() {
      return {
        'has-icon': this.icon,
      }
    },
    iconClasses() {
      return {
        'icon-left': this.iconPosition === 'left',
        'icon-right': this.iconPosition === 'right',
      }
    },
  },
}
</script>

<style>
.has-icon {
  /* Add some padding or styles when button has icon */
  padding-left: 1rem;
  padding-right: 1rem;
}

.icon-left {
  margin-right: 0.5rem;
}

.icon-right {
  margin-left: 0.5rem;
}
</style>
